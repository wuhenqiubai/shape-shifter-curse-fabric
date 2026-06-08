package net.onixary.shapeShifterCurseFabric.player_form.new_form_system;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ITransformReason {
    interface ITransformReasonWithArg <T> extends ITransformReason {
        T getArg();

        void setArg(T arg);
    }


    static ITransformReason create(Identifier reasonType, BiFunction<PlayerEntity, IForm, IForm> fNextForm, BiFunction<PlayerEntity, IForm, IForm> fPrevForm) {
        return new ITransformReason() {
            @Override
            public Identifier getReasonType() {
                return reasonType;
            }

            @Override
            public @Nullable IForm getFallBackNextForm(PlayerEntity player, IForm nowForm) {
                return fNextForm.apply(player, nowForm);
            }

            @Override
            public @Nullable IForm getFallBackPrevForm(PlayerEntity player, IForm nowForm) {
                return fPrevForm.apply(player, nowForm);
            }
        };
    }

    static <T> ITransformReasonWithArg<T> create(Identifier reasonType, ExtraFunctionInterface.TriFunction<ITransformReasonWithArg<T>, PlayerEntity, IForm, IForm> fNextForm, ExtraFunctionInterface.TriFunction<ITransformReasonWithArg<T>, PlayerEntity, IForm, IForm> fPrevForm, T arg) {
        return new ITransformReasonWithArg<>() {
	        private T storedArg = arg;

	        @Override
	        public T getArg() {
		        return storedArg;
	        }

	        @Override
	        public void setArg(T arg) {
		        storedArg = arg;
	        }

	        @Override
	        public Identifier getReasonType() {
		        return reasonType;
	        }

	        @Override
	        public @Nullable IForm getFallBackNextForm(PlayerEntity player, IForm nowForm) {
		        return fNextForm.apply(this, player, nowForm);
	        }

	        @Override
	        public @Nullable IForm getFallBackPrevForm(PlayerEntity player, IForm nowForm) {
		        return fPrevForm.apply(this, player, nowForm);
	        }
        };
    }

    Identifier InstinctReasonID = ShapeShifterCurseFabric.identifier("instinct");
    ITransformReason Instinct = create(InstinctReasonID,
            (player, nowForm) -> {
                IFormGroup group = nowForm.getFormGroup();
                int tier = nowForm.getFormTier() + 1;
                IForm result = null;
                if (group != null) {
                    result = group.getRandomForm(tier, player.getRandom(), FormUtils.NoInstinctTFTarget.hasFlag().negate());
                }
                return result == null ? nowForm : result;
            },
            (player, nowForm) -> {
                IForm prevForm = FormUtils.getPrevForm(player);
                int tier = nowForm.getFormTier() - 1;
                if (prevForm != null && prevForm.getFormTier() == tier) {
                    return prevForm;
                }
                IFormGroup group = nowForm.getFormGroup();
                IForm result = null;
                if (group != null) {
                    result = group.getRandomForm(tier, player.getRandom(), FormUtils.NoInstinctTFTarget.hasFlag().negate());
                }
                return result == null ? nowForm : result;
            }
    );
    Identifier CursedMoonReasonID = ShapeShifterCurseFabric.identifier("cursed_moon");
    ITransformReason CursedMoon = create(CursedMoonReasonID,
            (player, nowForm) -> {
                // TODO
                return nowForm;
            },
            (player, nowForm) -> {
                // TODO
                return nowForm;
            }
    );

    Identifier ItemReasonID = ShapeShifterCurseFabric.identifier("item");
    Function<ItemStack, ITransformReasonWithArg<ItemStack>> ItemReasonBuilder = (itemStack) -> create(ItemReasonID,
            (reason, player, nowForm) -> {
                // TODO
                return nowForm;
            },
            (reason, player, nowForm) -> {
                // TODO
                return nowForm;
            },
            itemStack
    );

    Identifier ForceReasonID = ShapeShifterCurseFabric.identifier("force");
    Function<IForm, ITransformReasonWithArg<IForm>> ForceReasonBuilder = (form) -> create(ForceReasonID,
            (reason, player, nowForm) -> reason.getArg(),
            (reason, player, nowForm) -> reason.getArg(),
            form
    );


    Identifier getReasonType();

    default @Nullable IForm getFallBackNextForm(PlayerEntity player, IForm nowForm) {
        return null;
    }

    default @Nullable IForm getFallBackPrevForm(PlayerEntity player, IForm nowForm) {
        return null;
    }
}
