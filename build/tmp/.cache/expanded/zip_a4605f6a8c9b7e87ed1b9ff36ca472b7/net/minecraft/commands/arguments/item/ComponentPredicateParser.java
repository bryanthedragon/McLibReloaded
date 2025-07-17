package net.minecraft.commands.arguments.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.NamedRule;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Scope;
import net.minecraft.util.parsing.packrat.Term;
import net.minecraft.util.parsing.packrat.commands.Grammar;
import net.minecraft.util.parsing.packrat.commands.ResourceLocationParseRule;
import net.minecraft.util.parsing.packrat.commands.ResourceLookupRule;
import net.minecraft.util.parsing.packrat.commands.StringReaderTerms;
import net.minecraft.util.parsing.packrat.commands.TagParseRule;

public class ComponentPredicateParser {
    public static <T, C, P> Grammar<List<T>> createGrammar(ComponentPredicateParser.Context<T, C, P> p_329972_) {
        Atom<List<T>> atom = Atom.of("top");
        Atom<Optional<T>> atom1 = Atom.of("type");
        Atom<Unit> atom2 = Atom.of("any_type");
        Atom<T> atom3 = Atom.of("element_type");
        Atom<T> atom4 = Atom.of("tag_type");
        Atom<List<T>> atom5 = Atom.of("conditions");
        Atom<List<T>> atom6 = Atom.of("alternatives");
        Atom<T> atom7 = Atom.of("term");
        Atom<T> atom8 = Atom.of("negation");
        Atom<T> atom9 = Atom.of("test");
        Atom<C> atom10 = Atom.of("component_type");
        Atom<P> atom11 = Atom.of("predicate_type");
        Atom<ResourceLocation> atom12 = Atom.of("id");
        Atom<Dynamic<?>> atom13 = Atom.of("tag");
        Dictionary<StringReader> dictionary = new Dictionary<>();
        NamedRule<StringReader, ResourceLocation> namedrule = dictionary.put(atom12, ResourceLocationParseRule.INSTANCE);
        NamedRule<StringReader, List<T>> namedrule1 = dictionary.put(
            atom,
            Term.alternative(
                Term.sequence(
                    dictionary.named(atom1),
                    StringReaderTerms.character('['),
                    Term.cut(),
                    Term.optional(dictionary.named(atom5)),
                    StringReaderTerms.character(']')
                ),
                dictionary.named(atom1)
            ),
            p_331933_ -> {
                Builder<T> builder = ImmutableList.builder();
                p_331933_.getOrThrow(atom1).ifPresent(builder::add);
                List<T> list = p_331933_.get(atom5);
                if (list != null) {
                    builder.addAll(list);
                }

                return builder.build();
            }
        );
        dictionary.put(
            atom1,
            Term.alternative(
                dictionary.named(atom3),
                Term.sequence(StringReaderTerms.character('#'), Term.cut(), dictionary.named(atom4)),
                dictionary.named(atom2)
            ),
            p_333155_ -> Optional.ofNullable(p_333155_.getAny(atom3, atom4))
        );
        dictionary.put(atom2, StringReaderTerms.character('*'), p_328666_ -> Unit.INSTANCE);
        dictionary.put(atom3, new ComponentPredicateParser.ElementLookupRule<>(namedrule, p_329972_));
        dictionary.put(atom4, new ComponentPredicateParser.TagLookupRule<>(namedrule, p_329972_));
        dictionary.put(
            atom5,
            Term.sequence(dictionary.named(atom6), Term.optional(Term.sequence(StringReaderTerms.character(','), dictionary.named(atom5)))),
            p_332096_ -> {
                T t = p_329972_.anyOf(p_332096_.getOrThrow(atom6));
                return Optional.ofNullable(p_332096_.get(atom5)).map(p_331681_ -> Util.copyAndAdd(t, (List<T>)p_331681_)).orElse(List.of(t));
            }
        );
        dictionary.put(
            atom6,
            Term.sequence(dictionary.named(atom7), Term.optional(Term.sequence(StringReaderTerms.character('|'), dictionary.named(atom6)))),
            p_334061_ -> {
                T t = p_334061_.getOrThrow(atom7);
                return Optional.ofNullable(p_334061_.get(atom6)).map(p_334416_ -> Util.copyAndAdd(t, (List<T>)p_334416_)).orElse(List.of(t));
            }
        );
        dictionary.put(
            atom7,
            Term.alternative(dictionary.named(atom9), Term.sequence(StringReaderTerms.character('!'), dictionary.named(atom8))),
            p_335341_ -> p_335341_.getAnyOrThrow(atom9, atom8)
        );
        dictionary.put(atom8, dictionary.named(atom9), p_331974_ -> p_329972_.negate(p_331974_.getOrThrow(atom9)));
        dictionary.putComplex(
            atom9,
            Term.alternative(
                Term.sequence(dictionary.named(atom10), StringReaderTerms.character('='), Term.cut(), dictionary.named(atom13)),
                Term.sequence(dictionary.named(atom11), StringReaderTerms.character('~'), Term.cut(), dictionary.named(atom13)),
                dictionary.named(atom10)
            ),
            p_389645_ -> {
                Scope scope = p_389645_.scope();
                P p = scope.get(atom11);

                try {
                    if (p != null) {
                        Dynamic<?> dynamic1 = scope.getOrThrow(atom13);
                        return p_329972_.createPredicateTest(p_389645_.input(), p, dynamic1);
                    } else {
                        C c = scope.getOrThrow(atom10);
                        Dynamic<?> dynamic = scope.get(atom13);
                        return dynamic != null ? p_329972_.createComponentTest(p_389645_.input(), c, dynamic) : p_329972_.createComponentTest(p_389645_.input(), c);
                    }
                } catch (CommandSyntaxException commandsyntaxexception) {
                    p_389645_.errorCollector().store(p_389645_.mark(), commandsyntaxexception);
                    return null;
                }
            }
        );
        dictionary.put(atom10, new ComponentPredicateParser.ComponentLookupRule<>(namedrule, p_329972_));
        dictionary.put(atom11, new ComponentPredicateParser.PredicateLookupRule<>(namedrule, p_329972_));
        dictionary.put(atom13, new TagParseRule<>(NbtOps.INSTANCE));
        return new Grammar<>(dictionary, namedrule1);
    }

    static class ComponentLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, C> {
        ComponentLookupRule(NamedRule<StringReader, ResourceLocation> p_393495_, ComponentPredicateParser.Context<T, C, P> p_336202_) {
            super(p_393495_, p_336202_);
        }

        @Override
        protected C validateElement(ImmutableStringReader p_335905_, ResourceLocation p_336332_) throws Exception {
            return this.context.lookupComponentType(p_335905_, p_336332_);
        }

        @Override
        public Stream<ResourceLocation> possibleResources() {
            return this.context.listComponentTypes();
        }
    }

    public interface Context<T, C, P> {
        T forElementType(ImmutableStringReader p_331849_, ResourceLocation p_335307_) throws CommandSyntaxException;

        Stream<ResourceLocation> listElementTypes();

        T forTagType(ImmutableStringReader p_332583_, ResourceLocation p_334980_) throws CommandSyntaxException;

        Stream<ResourceLocation> listTagTypes();

        C lookupComponentType(ImmutableStringReader p_331245_, ResourceLocation p_328438_) throws CommandSyntaxException;

        Stream<ResourceLocation> listComponentTypes();

        T createComponentTest(ImmutableStringReader p_331435_, C p_331254_, Dynamic<?> p_397796_) throws CommandSyntaxException;

        T createComponentTest(ImmutableStringReader p_333214_, C p_331519_);

        P lookupPredicateType(ImmutableStringReader p_329855_, ResourceLocation p_331711_) throws CommandSyntaxException;

        Stream<ResourceLocation> listPredicateTypes();

        T createPredicateTest(ImmutableStringReader p_332946_, P p_329900_, Dynamic<?> p_396949_) throws CommandSyntaxException;

        T negate(T p_328958_);

        T anyOf(List<T> p_330220_);
    }

    static class ElementLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, T> {
        ElementLookupRule(NamedRule<StringReader, ResourceLocation> p_391201_, ComponentPredicateParser.Context<T, C, P> p_333665_) {
            super(p_391201_, p_333665_);
        }

        @Override
        protected T validateElement(ImmutableStringReader p_336288_, ResourceLocation p_329752_) throws Exception {
            return this.context.forElementType(p_336288_, p_329752_);
        }

        @Override
        public Stream<ResourceLocation> possibleResources() {
            return this.context.listElementTypes();
        }
    }

    static class PredicateLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, P> {
        PredicateLookupRule(NamedRule<StringReader, ResourceLocation> p_397214_, ComponentPredicateParser.Context<T, C, P> p_335118_) {
            super(p_397214_, p_335118_);
        }

        @Override
        protected P validateElement(ImmutableStringReader p_334282_, ResourceLocation p_330262_) throws Exception {
            return this.context.lookupPredicateType(p_334282_, p_330262_);
        }

        @Override
        public Stream<ResourceLocation> possibleResources() {
            return this.context.listPredicateTypes();
        }
    }

    static class TagLookupRule<T, C, P> extends ResourceLookupRule<ComponentPredicateParser.Context<T, C, P>, T> {
        TagLookupRule(NamedRule<StringReader, ResourceLocation> p_397926_, ComponentPredicateParser.Context<T, C, P> p_330358_) {
            super(p_397926_, p_330358_);
        }

        @Override
        protected T validateElement(ImmutableStringReader p_335818_, ResourceLocation p_327854_) throws Exception {
            return this.context.forTagType(p_335818_, p_327854_);
        }

        @Override
        public Stream<ResourceLocation> possibleResources() {
            return this.context.listTagTypes();
        }
    }
}