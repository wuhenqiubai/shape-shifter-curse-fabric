#!/usr/bin/env python3
"""
Scan all mixins and verify @Inject/@Redirect/@ModifyArg targets against 1.21 bytecode.
Usage: python verify_mixins.py [--fix] [--dry-run]
"""

import json, os, re, subprocess, sys
from pathlib import Path
from collections import defaultdict

BASE = Path('src/main/java')
RESOURCES = Path('src/main/resources')
JAR = Path('.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-ba450d3941/1.21.1-net.fabricmc.yarn.1_21_1.1.21.1+build.3-v2/minecraft-merged-ba450d3941-1.21.1-net.fabricmc.yarn.1_21_1.1.21.1+build.3-v2.jar')

# Cache javap output
JAVAP_CACHE = {}

def javap(class_name):
    """Get javap output for a class."""
    if class_name in JAVAP_CACHE:
        return JAVAP_CACHE[class_name]
    try:
        result = subprocess.run(
            ['javap', '-p', '-cp', str(JAR), class_name],
            capture_output=True, text=True, timeout=30
        )
        JAVAP_CACHE[class_name] = result.stdout
        return result.stdout
    except Exception as e:
        print(f'  [WARN] javap failed for {class_name}: {e}')
        return ''

def javap_method_body(class_name, method_sig):
    """Get bytecode for a specific method."""
    cache_key = f'{class_name}::{method_sig}'
    if cache_key in JAVAP_CACHE:
        return JAVAP_CACHE[cache_key]
    try:
        result = subprocess.run(
            ['javap', '-c', '-p', '-cp', str(JAR), class_name],
            capture_output=True, text=True, timeout=30
        )
        JAVAP_CACHE[cache_key] = result.stdout
        return result.stdout
    except:
        return ''

def get_methods(javap_output):
    """Parse javap output to get method signatures."""
    methods = {}
    modifiers = '(?:public|private|protected|static|final|synchronized|native|abstract)'
    for line in javap_output.split('\n'):
        line = line.strip()
        if not line.endswith(';'):
            continue
        # Try method with return type: "public final [<T...>] ReturnType methodName(params);"
        # The modifier group must be repeatable to handle "public final", "public static final", etc.
        m = re.match(rf'\s*{modifiers}(?:\s+{modifiers})*\s+(?:<[^>]+>\s+)?([\w.$]+)\s+(\w+)\s*\(([^)]*)\)\s*;', line)
        if m:
            ret, name, params = m.groups()
            if ret not in ('public', 'private', 'protected', 'static', 'final', 'synchronized', 'native', 'abstract'):
                param_count = 0 if not params.strip() else len(params.split(','))
                methods[name] = {'return': ret, 'params': params.strip(), 'param_count': param_count, 'full': line.strip()}
                continue
        # Try constructor: "public ClassName(params);"
        m = re.match(rf'\s*{modifiers}(?:\s+{modifiers})*\s+([\w.$]+)\s*\(([^)]*)\)\s*;', line)
        if m:
            cls, params = m.groups()
            name = cls.split('.')[-1]
            param_count = 0 if not params.strip() else len(params.split(','))
            methods[name] = {'return': '<init>', 'params': params.strip(), 'param_count': param_count, 'full': line.strip()}
            # Also register as <init>
            if '<init>' not in methods:
                methods['<init>'] = methods[name]
    return methods

def get_invoke_targets(bytecode, method_name):
    """Get all INVOKE targets in a method's bytecode."""
    targets = []
    in_method = False
    for line in bytecode.split('\n'):
        if f'{method_name}(' in line and ('Method' in line or 'Method' in line):
            in_method = True
            continue
        if in_method:
            if line.strip().startswith('}') or (line.strip() and not line.startswith(' ') and ':' not in line):
                break
            m = re.search(r'(?:INVOKEVIRTUAL|INVOKESTATIC|INVOKEINTERFACE|INVOKESPECIAL)\s+#\d+\s+//\s+Method\s+(\S+)\.(\w+):\(([^)]*)\)', line)
            if m:
                cls, name, params = m.groups()
                targets.append({'class': cls.replace('/', '.'), 'name': name, 'params': params.strip()})
    return targets

def get_class_from_inject_target(target_str):
    """Parse 'Lcom/example/Class;methodName(Lparam;)V' into {class, method, params}."""
    m = re.match(r'L([^;]+);(\w+)\(([^)]*)\)(.*)', target_str)
    if m:
        cls = m.group(1).replace('/', '.')
        method = m.group(2)
        params = m.group(3)
        return cls, method, params
    return None, None, None

def class_to_jvm(name):
    """Convert dot-separated class name to JVM internal format."""
    return name.replace('.', '/')

def find_mixins():
    """Find all mixin configs and their mixin classes."""
    mixins = []
    for config_file in RESOURCES.glob('**/shape-shifter-curse.mixins.json'):
        config = json.load(open(config_file))
        pkg = config.get('package', '')
        for section in ['mixins', 'client', 'server']:
            for name in config.get(section, []):
                full = f'{pkg}.{name}'
                # Handle inner classes (Foo$Bar → Foo.java with inner class)
                if '$' in name:
                    file_name = name.split('$')[0] + '.java'
                else:
                    file_name = name + '.java'
                mixins.append({'full': full, 'file': file_name, 'section': section})
    return mixins

def resolve_class(short_name, imports):
    """Resolve a short class name to fully qualified using imports."""
    if short_name in imports:
        return imports[short_name]
    # Try common java.lang / primitive
    if short_name in ('String', 'Object', 'Integer', 'Boolean', 'Float', 'Double', 'Long', 'Short', 'Byte', 'Character'):
        return f'java.lang.{short_name}'
    return short_name


def parse_imports(lines):
    """Parse import statements from Java source."""
    imports = {}
    for line in lines:
        m = re.match(r'^import\s+((?:static\s+)?[\w.]+(?:\.[\w*]+)?)\s*;', line)
        if m:
            full = m.group(1)
            if full.startswith('static '):
                full = full[7:]
            # Get simple name
            parts = full.split('.')
            if '*' in parts[-1]:
                continue  # skip wildcard imports
            imports[parts[-1]] = full
    return imports


def scan_mixin_file(file_name):
    """Scan a mixin Java file for annotations."""
    found = []
    java_file = None
    for f in BASE.rglob(file_name):
        java_file = f
        break
    if not java_file:
        return found

    content = java_file.read_text(encoding='utf-8')
    lines = content.split('\n')
    imports = parse_imports(lines)
    pkg_match = re.search(r'^package\s+([\w.]+)\s*;', content)
    pkg = pkg_match.group(1) if pkg_match else ''

    # Find @Mixin target — need to find the CORRECT one for each annotation
    # Build a map of {line_number: mixin_target} by scanning for inner class @Mixin
    mixin_targets = {}  # line -> target
    current_target = None
    for i, line in enumerate(lines):
        m = re.search(r'@Mixin\s*\(\s*(?:value\s*=\s*)?\{?([^})]+)\}?\s*\)', line)
        if m:
            targets = re.findall(r'(\w+(?:\.\w+)*)\.class', m.group(1))
            if targets:
                short = targets[0]
                full = resolve_class(short, imports)
                if '.' not in full:
                    for pfx in ['net.minecraft.entity.player.', 'net.minecraft.entity.',
                                'net.minecraft.client.network.', 'net.minecraft.server.network.',
                                'net.minecraft.client.render.entity.', 'net.minecraft.client.render.',
                                'net.minecraft.client.gui.screen.ingame.', 'net.minecraft.client.gui.',
                                'net.minecraft.screen.', 'net.minecraft.recipe.',
                                'net.minecraft.enchantment.', 'net.minecraft.item.',
                                'net.minecraft.entity.mob.', 'net.minecraft.entity.projectile.',
                                'net.minecraft.entity.effect.', 'net.minecraft.world.',
                                'net.minecraft.client.', 'net.minecraft.']:
                        full = pfx + short
                        break
                current_target = full
                mixin_targets[i] = full

    # For each annotation, find the nearest preceding @Mixin target
    def get_mixin_target(ann_line):
        best = None
        for ml, mt in sorted(mixin_targets.items()):
            if ml < ann_line:
                best = mt
        return best

    # Find @Inject, @Redirect, @ModifyArg annotations and their methods
    current_annotation = None
    for i, line in enumerate(lines):
        # Detect annotation
        for ann in ['@Inject', '@Redirect', '@ModifyArg']:
            if ann in line and ('method' in line or 'target' in line):
                # Merge with continuation lines
                full_ann = line
                j = i
                while j + 1 < len(lines) and not lines[j].strip().endswith(')') and not lines[j].strip().endswith('})'):
                    j += 1
                    if j < len(lines):
                        full_ann += ' ' + lines[j].strip()
                        if lines[j].strip().endswith(')') or lines[j].strip().endswith('})'):
                            break

                # Parse method name
                method_match = re.search(r'method\s*=\s*"(.*?)"', full_ann)
                method_name = method_match.group(1) if method_match else None

                # Parse at target
                at_match = re.search(r'at\s*=\s*@At\s*\(([^)]*)\)', full_ann)
                at_target = None
                if at_match:
                    t = re.search(r'target\s*=\s*"(.*?)"', at_match.group(1))
                    if t:
                        at_target = t.group(1)

                # Find the method that follows this annotation
                method_line = None
                mixin_method_name = None
                for k in range(i + 1, min(i + 15, len(lines))):
                    m2 = re.match(r'\s*(?:private|public|protected|static)\s+\S+\s+(\w+)\s*\(', lines[k])
                    if m2:
                        method_line = k
                        mixin_method_name = m2.group(1)
                        break

                ann_target = get_mixin_target(i)
                found.append({
                    'file': str(java_file),
                    'line': i + 1,
                    'annotation': ann,
                    'method': method_name,
                    'at_target': at_target,
                    'mixin_method': mixin_method_name,
                    'mixin_target': ann_target,
                    'mixin_method_line': method_line,
                    'full_annotation': full_ann.strip(),
                })
                break  # Only one annotation per line

    return found

def check_mixin(entry, fix=False, dry_run=True):
    """Check a single mixin annotation entry."""
    issues = []

    target_class = entry.get('mixin_target')
    if not target_class:
        return issues

    raw_method = entry.get('method')
    at_target = entry.get('at_target')
    if not raw_method:
        return issues

    # Strip parameter signature from method name (e.g., "foo(LX;)V" -> "foo")
    # Also handle "*" suffix for overloaded methods
    method_name = raw_method
    if '(' in method_name:
        method_name = method_name[:method_name.index('(')]
    if method_name.endswith('*'):
        method_name = method_name[:-1]

    # Handle <init> (constructor) — both '<init>' and the class short name work
    if method_name == '<init>':
        pass  # get_methods now registers both

    javap_out = javap(target_class)
    methods = get_methods(javap_out)

    # Check target method exists
    if method_name and method_name not in methods:
        # Check if it's an inner class mixin — the actual @Mixin might be on an inner class
        issues.append(f"METHOD_NOT_FOUND: {target_class}.{method_name}() — raw: {raw_method}")
        return issues

    # For @Redirect: check INVOKE target exists in method bytecode
    if entry['annotation'] == '@Redirect' and at_target:
        bytecode = javap_method_body(target_class, method_name)
        targets = get_invoke_targets(bytecode, method_name)
        found = any(t['name'] == at_target.split('.')[-1].split('(')[0] for t in targets)
        if not found:
            issues.append(f"INVOKE_NOT_FOUND: {at_target} in {target_class}.{method_name}()")
            return issues

    return issues

def fix_comment_out(file_path, start_line, end_line=None):
    """Comment out a range of lines in a Java file."""
    content = Path(file_path).read_text(encoding='utf-8').split('\n')
    if end_line is None:
        end_line = start_line
    # Find the method body - from annotation to closing brace
    # Simple approach: comment from annotation line to next blank line or next annotation
    # Better: find the method by looking for the method signature after annotation
    modified = False
    for i in range(start_line - 1, min(start_line + 30, len(content))):
        line = content[i]
        if re.match(r'\s*(?:private|public|protected|static)\s+\S+\s+\w+\s*\(', line):
            # This is the method signature - comment out from annotation to end of method
            # Find matching }
            depth = 0
            started = False
            end = i
            for j in range(i, min(i + 100, len(content))):
                if '{' in content[j]:
                    depth += content[j].count('{')
                    started = True
                if '}' in content[j]:
                    depth -= content[j].count('}')
                if started and depth == 0:
                    end = j
                    break
            # Comment out annotation line and method
            for j in range(start_line - 1, end + 1):
                if not content[j].strip().startswith('//'):
                    content[j] = '// ' + content[j]
            modified = True
            break
    if modified:
        Path(file_path).write_text('\n'.join(content), encoding='utf-8')
    return modified


def main():
    dry_run = '--dry-run' in sys.argv
    do_fix = '--fix' in sys.argv

    print("=== Scanning mixins and verifying against 1.21 bytecode ===\n")

    mixins = find_mixins()
    print(f"Found {len(mixins)} mixin entries in configs.\n")

    all_entries = []
    seen_files = set()
    for m in mixins:
        if m['file'] in seen_files:
            continue
        seen_files.add(m['file'])
        entries = scan_mixin_file(m['file'])
        all_entries.extend(entries)

    print(f"Found {len(all_entries)} @Inject/@Redirect/@ModifyArg annotations.\n")

    issues = []
    for entry in all_entries:
        result = check_mixin(entry)
        if result:
            for issue in result:
                issues.append((entry, issue))

    # Group by file
    by_file = defaultdict(list)
    for entry, issue in issues:
        by_file[entry['file']].append((entry, issue))

    if not issues:
        print("All mixin targets verified successfully!")
        return

    print(f"=== Found {len(issues)} issues in {len(by_file)} files ===\n")

    for file_path, file_issues in sorted(by_file.items()):
        print(f"\n{file_path}:")
        for entry, issue in file_issues:
            print(f"  L{entry['line']}: {entry['annotation']} method={entry['method']} target={entry.get('at_target','')}")
            print(f"    → {issue}")

            if do_fix and 'METHOD_NOT_FOUND' in issue:
                if not dry_run:
                    fix_comment_out(entry['file'], entry['line'])
                    print(f"    [FIXED] Commented out")

            elif do_fix and 'INVOKE_NOT_FOUND' in issue:
                if not dry_run:
                    fix_comment_out(entry['file'], entry['line'])
                    print(f"    [FIXED] Commented out")


if __name__ == '__main__':
    main()
