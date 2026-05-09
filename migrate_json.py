#!/usr/bin/env python3
"""
Migrate Apoli/Origins JSON files from alpha 5-12 API changes.
Processes data/*/powers/, data/*/origins/, data/*/origin_layers/, data/*/badges/,
data/*/global_powers/, and data/*/enchantment/ directories.

Usage: python migrate_json.py [--dry-run] [base_dir]
"""

import json
import os
import sys
import copy

TARGET_DIRS = ['powers', 'origins', 'origin_layers', 'badges', 'global_powers', 'enchantment']


def is_item_stack_like(obj):
    """Check if object looks like an item stack (has 'item'/'id' string field)."""
    if not isinstance(obj, dict):
        return False
    item_val = obj.get('item') or obj.get('id')
    return isinstance(item_val, str)


def is_status_effect_instance(obj):
    """Check if object looks like a status effect instance."""
    if not isinstance(obj, dict):
        return False
    has_effect = isinstance(obj.get('effect'), str) or isinstance(obj.get('id'), str)
    has_duration = 'duration' in obj
    has_amplifier = 'amplifier' in obj
    return has_effect and (has_duration or has_amplifier)


def is_attributed_modifier(obj):
    """Check if object looks like an attributed attribute modifier (has 'name'/'id', 'attribute', 'operation')."""
    if not isinstance(obj, dict):
        return False
    has_name = 'name' in obj or 'id' in obj
    has_attr = 'attribute' in obj
    has_op = 'operation' in obj
    return has_name and has_attr and has_op


def is_modifier(obj):
    """Check if object looks like a non-attributed modifier (has 'operation' and 'value'/'amount')."""
    if not isinstance(obj, dict):
        return False
    has_val = 'value' in obj or 'amount' in obj
    has_op = 'operation' in obj
    # Exclude attributed modifiers (those have 'name' or 'id' with 'attribute')
    has_name_or_attr = ('name' in obj and 'attribute' in obj) or ('id' in obj and 'attribute' in obj)
    return has_val and has_op and not has_name_or_attr


def is_food_component(obj):
    """Check if object looks like a food component."""
    if not isinstance(obj, dict):
        return False
    return ('hunger' in obj or 'nutrition' in obj) and 'saturation' in obj


def name_to_id_value(name_str, namespace='shape-shifter-curse'):
    """Convert human-readable modifier name to namespaced identifier."""
    import re
    clean = name_str.lower().strip()
    clean = re.sub(r'[^a-z0-9_/.-]', '_', clean)
    clean = re.sub(r'_+', '_', clean).strip('_')
    return f'{namespace}:{clean}'


def get_namespace_from_path(filepath):
    """Extract namespace from data/<namespace>/... path."""
    parts = filepath.replace('\\', '/').split('/')
    try:
        data_idx = parts.index('data')
        if data_idx + 1 < len(parts):
            return parts[data_idx + 1]
    except ValueError:
        pass
    return 'shape-shifter-curse'


def transform(obj, modified, namespace='shape-shifter-curse'):
    """Recursively transform JSON object. Returns transformed object and mutation flag."""
    if isinstance(obj, dict):
        new_obj = {}
        keys_to_process = list(obj.keys())

        # --- Status effect instance: effect→id, is_ambient→ambient ---
        if is_status_effect_instance(obj):
            modified = True
            for k, v in obj.items():
                if k == 'effect':
                    new_obj['id'] = v
                elif k == 'is_ambient':
                    new_obj['ambient'] = v
                else:
                    new_obj[k], _ = transform(v, False, namespace)
            return new_obj, True

        # --- Food component: hunger→nutrition, always_edible→can_always_eat, snack→eat_seconds ---
        if is_food_component(obj):
            modified = True
            for k, v in obj.items():
                if k == 'hunger':
                    new_obj['nutrition'] = v
                elif k == 'always_edible':
                    new_obj['can_always_eat'] = v
                elif k == 'snack':
                    # true → 0.8 eat_seconds, false → 1.6 (default)
                    new_obj['eat_seconds'] = 0.8 if v else 1.6
                else:
                    new_obj[k], _ = transform(v, False, namespace)
            return new_obj, True

        # --- Attributed modifier: name→id, value→amount ---
        if is_attributed_modifier(obj):
            modified = True
            for k, v in obj.items():
                if k == 'name':
                    new_obj['id'] = name_to_id_value(v, namespace)
                elif k == 'value':
                    new_obj['amount'], _ = transform(v, False, namespace)
                elif k == 'operation':
                    new_obj[k], _ = transform_operation_value(v)
                    modified = True
                else:
                    new_obj[k], _ = transform(v, False, namespace)
            return new_obj, True

        # --- Non-attributed modifier: value→amount, strip name ---
        if is_modifier(obj):
            modified = True
            for k, v in obj.items():
                if k == 'name':
                    # Drop name from non-attributed modifiers (not valid in alpha 5+)
                    modified = True
                    continue
                if k == 'value':
                    new_obj['amount'], _ = transform(v, False, namespace)
                elif k == 'operation':
                    new_obj[k], _ = transform_operation_value(v)
                    modified = True
                else:
                    new_obj[k], _ = transform(v, False, namespace)
            return new_obj, True

        # --- Item stack: item→id, amount→count ---
        if is_item_stack_like(obj):
            modified = True
            for k, v in obj.items():
                if k == 'item':
                    new_obj['id'] = v
                elif k == 'amount':
                    new_obj['count'] = v
                else:
                    new_obj[k], _ = transform(v, False, namespace)
            return new_obj, True

        # --- spawn_effect_cloud: effect/effects → effect_component.custom_effects ---
        if obj.get('type') == 'origins:spawn_effect_cloud' and ('effect' in obj or 'effects' in obj):
            modified = True
            for k, v in obj.items():
                if k == 'effect':
                    # Single effect → wrap in array
                    inner, _ = transform(v, False, namespace)
                    new_obj['effect_component'] = {'custom_effects': [inner]}
                elif k == 'effects':
                    inner_list, _ = transform(v, False, namespace)
                    new_obj['effect_component'] = {'custom_effects': inner_list}
                else:
                    new_obj[k], _ = transform(v, False, namespace)
            return new_obj, True

        # --- Generic processing ---
        for k, v in obj.items():
            if isinstance(v, str):
                new_val, str_modified = transform_string_value(v)
                if str_modified:
                    modified = True
                    new_obj[k] = new_val
                else:
                    new_obj[k] = v
            elif isinstance(v, (dict, list)):
                new_obj[k], child_modified = transform(v, False, namespace)
                if child_modified:
                    modified = True
            else:
                new_obj[k] = v

        return new_obj, modified

    elif isinstance(obj, list):
        new_list = []
        modified = False
        for item in obj:
            new_item, item_modified = transform(item, False, namespace)
            new_list.append(new_item)
            if item_modified:
                modified = True
        return new_list, modified

    elif isinstance(obj, str):
        return transform_string_value(obj)

    return obj, modified


def transform_operation_value(val):
    """Transform operation string values."""
    if not isinstance(val, str):
        return val, False
    ops = {
        'addition': 'add_value',
        'multiply_base': 'add_multiplied_base',
        'multiply_total': 'add_multiplied_total',
    }
    if val in ops:
        return ops[val], True
    return val, False


def transform_string_value(val):
    """Transform string values for known patterns."""
    if not isinstance(val, str):
        return val, False

    # Meta condition type renames
    if val == 'origins:and':
        return 'origins:all_of', True
    if val == 'origins:or':
        return 'origins:any_of', True

    # Operation value renames (standalone strings — unlikely but safe)
    return transform_operation_value(val)


def should_process_file(filepath):
    """Check if file is under data/<namespace>/<type>/ where <type> is a target dir."""
    parts = filepath.replace('\\', '/').split('/')
    # Find 'data' index, then check the directory two levels deeper
    # Pattern: .../data/<namespace>/<type>/...
    try:
        data_idx = parts.index('data')
        if data_idx + 2 < len(parts):
            type_dir = parts[data_idx + 2]
            return type_dir in TARGET_DIRS
    except ValueError:
        pass
    return False


def process_file(filepath, dry_run=False):
    """Process a single JSON file."""
    if not should_process_file(filepath):
        return False

    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)
    except json.JSONDecodeError as e:
        print(f"  SKIP: {filepath} (invalid JSON: {e})")
        return False
    except Exception as e:
        print(f"  SKIP: {filepath} ({e})")
        return False

    ns = get_namespace_from_path(filepath)
    transformed, modified = transform(data, False, namespace=ns)

    if not modified:
        return False

    if dry_run:
        print(f"  WOULD MODIFY: {filepath}")
        return True
    else:
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(transformed, f, indent=2, ensure_ascii=False)
            f.write('\n')
        print(f"  MODIFIED: {filepath}")
        return True


def main():
    dry_run = '--dry-run' in sys.argv
    base_dir = sys.argv[-1] if len(sys.argv) > 1 and not sys.argv[-1].startswith('--') else 'src/main/resources'

    if not os.path.isdir(base_dir):
        print(f"Error: directory not found: {base_dir}")
        sys.exit(1)

    print(f"{'DRY RUN ' if dry_run else ''}Processing JSON files in: {base_dir}")
    print(f"Target directories: {TARGET_DIRS}")
    print()

    count = 0
    for root, dirs, files in os.walk(base_dir):
        for filename in files:
            if filename.endswith('.json'):
                filepath = os.path.join(root, filename)
                if process_file(filepath, dry_run=dry_run):
                    count += 1

    print(f"\n{'Would modify' if dry_run else 'Modified'} {count} files.")


if __name__ == '__main__':
    main()
