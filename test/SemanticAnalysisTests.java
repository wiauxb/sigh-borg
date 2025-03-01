import norswap.autumn.AutumnTestFixture;
import norswap.autumn.positions.LineMapString;
import norswap.sigh.SemanticAnalysis;
import norswap.sigh.SighGrammar;
import norswap.sigh.ast.SighNode;
import norswap.uranium.Reactor;
import norswap.uranium.UraniumTestFixture;
import norswap.utils.visitors.Walker;
import org.testng.annotations.Test;

/**
 * NOTE(norswap): These tests were derived from the {@link InterpreterTests} and don't test anything
 * more, but show how to idiomatically test semantic analysis. using {@link UraniumTestFixture}.
 */
public final class SemanticAnalysisTests extends UraniumTestFixture
{
    // ---------------------------------------------------------------------------------------------

    private final SighGrammar grammar = new SighGrammar();
    private final AutumnTestFixture autumnFixture = new AutumnTestFixture();

    {
        autumnFixture.rule = grammar.root();
        autumnFixture.runTwice = false;
        autumnFixture.bottomClass = this.getClass();
    }

    private String input;

    @Override protected Object parse (String input) {
        this.input = input;
        return autumnFixture.success(input).topValue();
    }

    @Override protected String astNodeToString (Object ast) {
        LineMapString map = new LineMapString("<test>", input);
        return ast.toString() + " (" + ((SighNode) ast).span.startString(map) + ")";
    }

    // ---------------------------------------------------------------------------------------------

    @Override protected void configureSemanticAnalysis (Reactor reactor, Object ast) {
        Walker<SighNode> walker = SemanticAnalysis.createWalker(reactor);
        walker.walk(((SighNode) ast));
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testLiteralsAndUnary() {
        successInput("return 42");
        successInput("return 42.0");
        successInput("return \"hello\"");
        successInput("return (42)");
        successInput("return [1, 2, 3]");
        successInput("return [[1, 2, 3],[4, 5, 6]]");
        successInput("return [0](5)");
        successInput("return [0](6, 2)");
        successInput("return true");
        successInput("return false");
        successInput("return null");
        successInput("return !false");
        successInput("return !true");
        successInput("return !!true");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testNumericBinary() {
        successInput("return 1 + 2");
        successInput("return 2 - 1");
        successInput("return 2 * 3");
        successInput("return 2 / 3");
        successInput("return 3 / 2");
        successInput("return 2 % 3");
        successInput("return 3 % 2");

        successInput("return 1.0 + 2.0");
        successInput("return 2.0 - 1.0");
        successInput("return 2.0 * 3.0");
        successInput("return 2.0 / 3.0");
        successInput("return 3.0 / 2.0");
        successInput("return 2.0 % 3.0");
        successInput("return 3.0 % 2.0");

        successInput("return 1 + 2.0");
        successInput("return 2 - 1.0");
        successInput("return 2 * 3.0");
        successInput("return 2 / 3.0");
        successInput("return 3 / 2.0");
        successInput("return 2 % 3.0");
        successInput("return 3 % 2.0");

        successInput("return 1.0 + 2");
        successInput("return 2.0 - 1");
        successInput("return 2.0 * 3");
        successInput("return 2.0 / 3");
        successInput("return 3.0 / 2");
        successInput("return 2.0 % 3");
        successInput("return 3.0 % 2");

        successInput("return 2 + [1]");
        successInput("return [1] + 2");

        failureInputWith("return 2 + true", "Trying to add Int with Bool");
        failureInputWith("return true + 2", "Trying to add Bool with Int");
//        failureInputWith("return 2 + [1]", "Trying to add Int with Int[]");
//        failureInputWith("return [1] + 2", "Trying to add Int[] with Int");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testArrayBinary() {
        successInput("return [1] + [2]");
        successInput("return [2] - [1]");
        successInput("return [2] * [3]");
        successInput("return [2] / [3]");
        successInput("return [3] / [2]");
        successInput("return [2] % [3]");
        successInput("return [3] % [2]");

        successInput("return [1.0] + [2.0]");
        successInput("return [2.0] - [1.0]");
        successInput("return [2.0] * [3.0]");
        successInput("return [2.0] / [3.0]");
        successInput("return [3.0] / [2.0]");
        successInput("return [2.0] % [3.0]");
        successInput("return [3.0] % [2.0]");

        successInput("return [1] + [2.0]");
        successInput("return [2] - [1.0]");
        successInput("return [2] * [3.0]");
        successInput("return [2] / [3.0]");
        successInput("return [3] / [2.0]");
        successInput("return [2] % [3.0]");
        successInput("return [3] % [2.0]");

        successInput("return [1.0] + [2]");
        successInput("return [2.0] - [1]");
        successInput("return [2.0] * [3]");
        successInput("return [2.0] / [3]");
        successInput("return [3.0] / [2]");
        successInput("return [2.0] % [3]");
        successInput("return [3.0] % [2]");

        successInput("return 2 + [1]");
        successInput("return [1] + 2");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testMatrixBinary() {
        successInput("return [[1]] + [[2]]");
        successInput("return [[2]] - [[1]]");
        successInput("return [[2]] * [[3]]");
        successInput("return [[2]] / [[3]]");
        successInput("return [[3]] / [[2]]");
        successInput("return [[2]] % [[3]]");
        successInput("return [[3]] % [[2]]");

        successInput("return [[1.0]] + [[2.0]]");
        successInput("return [[2.0]] - [[1.0]]");
        successInput("return [[2.0]] * [[3.0]]");
        successInput("return [[2.0]] / [[3.0]]");
        successInput("return [[3.0]] / [[2.0]]");
        successInput("return [[2.0]] % [[3.0]]");
        successInput("return [[3.0]] % [[2.0]]");

        successInput("return [[1]] + [[2.0]]");
        successInput("return [[2]] - [[1.0]]");
        successInput("return [[2]] * [[3.0]]");
        successInput("return [[2]] / [[3.0]]");
        successInput("return [[3]] / [[2.0]]");
        successInput("return [[2]] % [[3.0]]");
        successInput("return [[3]] % [[2.0]]");

        successInput("return [[1.0]] + [[2]]");
        successInput("return [[2.0]] - [[1]]");
        successInput("return [[2.0]] * [[3]]");
        successInput("return [[2.0]] / [[3]]");
        successInput("return [[3.0]] / [[2]]");
        successInput("return [[2.0]] % [[3]]");
        successInput("return [[3.0]] % [[2]]");

        successInput("return 2 + [[1]]");
        successInput("return [[1]] + 2");

        failureInputWith("return [[\"a\"]] * [[1]]", "Trying to multiply Mat#String with Mat#Int");
        failureInputWith("return [[\"a\"]] * [[1.0]]", "Trying to multiply Mat#String with Mat#Float");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testArrayMatrixBinary() {
        successInput("return [1] + [[2]]");
        successInput("return [2] - [[1]]");
        successInput("return [2] * [[3]]");
        successInput("return [2] / [[3]]");
        successInput("return [3] / [[2]]");
        successInput("return [2] % [[3]]");
        successInput("return [3] % [[2]]");

        successInput("return [1.0] + [[2.0]]");
        successInput("return [2.0] - [[1.0]]");
        successInput("return [2.0] * [[3.0]]");
        successInput("return [2.0] / [[3.0]]");
        successInput("return [3.0] / [[2.0]]");
        successInput("return [2.0] % [[3.0]]");
        successInput("return [3.0] % [[2.0]]");

        successInput("return [1] + [[2.0]]");
        successInput("return [2] - [[1.0]]");
        successInput("return [2] * [[3.0]]");
        successInput("return [2] / [[3.0]]");
        successInput("return [3] / [[2.0]]");
        successInput("return [2] % [[3.0]]");
        successInput("return [3] % [[2.0]]");

        successInput("return [1.0] + [[2]]");
        successInput("return [2.0] - [[1]]");
        successInput("return [2.0] * [[3]]");
        successInput("return [2.0] / [[3]]");
        successInput("return [3.0] / [[2]]");
        successInput("return [2.0] % [[3]]");
        successInput("return [3.0] % [[2]]");

        successInput("return [2] + [[1]]");
        successInput("return [[1]] + [2]");

        failureInputWith("return [\"a\"] * [[1]]", "Trying to multiply String[] with Mat#Int");
        failureInputWith("return [[\"a\"]] * [1]", "Trying to multiply Mat#String with Int[]");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testOtherBinary() {
        successInput("return true && false");
        successInput("return false && true");
        successInput("return true && true");
        successInput("return true || false");
        successInput("return false || true");
        successInput("return false || false");

        failureInputWith("return false || 1",
            "Attempting to perform binary logic on non-boolean type: Int");
        failureInputWith("return 2 || true",
            "Attempting to perform binary logic on non-boolean type: Int");

        successInput("return 1 + \"a\"");
        successInput("return \"a\" + 1");
        successInput("return \"a\" + true");

        successInput("return 1 == 1");
        successInput("return 1 == 2");
        successInput("return 1.0 == 1.0");
        successInput("return 1.0 == 2.0");
        successInput("return true == true");
        successInput("return false == false");
        successInput("return true == false");
        successInput("return 1 == 1.0");

        failureInputWith("return true == 1", "Trying to compare incomparable types Bool and Int");
        failureInputWith("return 2 == false", "Trying to compare incomparable types Int and Bool");

        successInput("return \"hi\" == \"hi\"");
        failureInputWith("return [1] == [1]", "Trying to compare incomparable types Int[] and Int[]");

        successInput("return 1 != 1");
        successInput("return 1 != 2");
        successInput("return 1.0 != 1.0");
        successInput("return 1.0 != 2.0");
        successInput("return true != true");
        successInput("return false != false");
        successInput("return true != false");
        successInput("return 1 != 1.0");

        failureInputWith("return true != 1", "Trying to compare incomparable types Bool and Int");
        failureInputWith("return 2 != false", "Trying to compare incomparable types Int and Bool");

        successInput("return \"hi\" != \"hi\"");
        failureInputWith("return [1] != [1]", "Trying to compare incomparable types Int[] and Int[]");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testComparaisonArray(){

        successInput("return [1] <=> [1]");
        successInput("return [1] <=> [2]");
        successInput("return [1.0] <=> [1.0]");
        successInput("return [1.0] <=> [2.0]");
        successInput("return [1] <=> [1.0]");
        successInput("return [1] <=> [2.0]");
        successInput("return [1.0] <=> [1]");
        successInput("return [1.0] <=> [2]");

        successInput("return [1] !<=> [1]");
        successInput("return [1] !<=> [2]");
        successInput("return [1.0] !<=> [1.0]");
        successInput("return [1.0] !<=> [2.0]");
        successInput("return [1] !<=> [1.0]");
        successInput("return [1] !<=> [2.0]");
        successInput("return [1.0] !<=> [1]");
        successInput("return [1.0] !<=> [2]");

        successInput("return [1] =? [1]");
        successInput("return [1] =? [2]");
        successInput("return [1.0] =? [1.0]");
        successInput("return [1.0] =? [2.0]");
        successInput("return [1] =? [1.0]");
        successInput("return [1] =? [2.0]");
        successInput("return [1.0] =? [1]");
        successInput("return [1.0] =? [2]");

        successInput("return [1] !=? [1]");
        successInput("return [1] !=? [2]");
        successInput("return [1.0] !=? [1.0]");
        successInput("return [1.0] !=? [2.0]");
        successInput("return [1] !=? [1.0]");
        successInput("return [1] !=? [2.0]");
        successInput("return [1.0] !=? [1]");
        successInput("return [1.0] !=? [2]");

        successInput("return [1] << [1]");
        successInput("return [1] << [2]");
        successInput("return [1.0] << [1.0]");
        successInput("return [1.0] << [2.0]");
        successInput("return [1] << [1.0]");
        successInput("return [1] << [2.0]");
        successInput("return [1.0] << [1]");
        successInput("return [1.0] << [2]");

        successInput("return [1] <? [1]");
        successInput("return [1] <? [2]");
        successInput("return [1.0] <? [1.0]");
        successInput("return [1.0] <? [2.0]");
        successInput("return [1] <? [1.0]");
        successInput("return [1] <? [2.0]");
        successInput("return [1.0] <? [1]");
        successInput("return [1.0] <? [2]");

        successInput("return [1] >> [1]");
        successInput("return [1] >> [2]");
        successInput("return [1.0] >> [1.0]");
        successInput("return [1.0] >> [2.0]");
        successInput("return [1] >> [1.0]");
        successInput("return [1] >> [2.0]");
        successInput("return [1.0] >> [1]");
        successInput("return [1.0] >> [2]");

        successInput("return [1] >? [1]");
        successInput("return [1] >? [2]");
        successInput("return [1.0] >? [1.0]");
        successInput("return [1.0] >? [2.0]");
        successInput("return [1] >? [1.0]");
        successInput("return [1] >? [2.0]");
        successInput("return [1.0] >? [1]");
        successInput("return [1.0] >? [2]");

        successInput("return [1] <<= [1]");
        successInput("return [1] <<= [2]");
        successInput("return [1.0] <<= [1.0]");
        successInput("return [1.0] <<= [2.0]");
        successInput("return [1] <<= [1.0]");
        successInput("return [1] <<= [2.0]");
        successInput("return [1.0] <<= [1]");
        successInput("return [1.0] <<= [2]");

        successInput("return [1] <=? [1]");
        successInput("return [1] <=? [2]");
        successInput("return [1.0] <=? [1.0]");
        successInput("return [1.0] <=? [2.0]");
        successInput("return [1] <=? [1.0]");
        successInput("return [1] <=? [2.0]");
        successInput("return [1.0] <=? [1]");
        successInput("return [1.0] <=? [2]");

        successInput("return [1] >>= [1]");
        successInput("return [1] >>= [2]");
        successInput("return [1.0] >>= [1.0]");
        successInput("return [1.0] >>= [2.0]");
        successInput("return [1] >>= [1.0]");
        successInput("return [1] >>= [2.0]");
        successInput("return [1.0] >>= [1]");
        successInput("return [1.0] >>= [2]");

        successInput("return [1] >=? [1]");
        successInput("return [1] >=? [2]");
        successInput("return [1.0] >=? [1.0]");
        successInput("return [1.0] >=? [2.0]");
        successInput("return [1] >=? [1.0]");
        successInput("return [1] >=? [2.0]");
        successInput("return [1.0] >=? [1]");
        successInput("return [1.0] >=? [2]");

        failureInputWith("return [1](2, 2) >> 2",
            "Attempting to perform arithmetic comparison on non-arraylike type: Int");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testComparaisonMatrix(){

        successInput("return [[1]] <=> [[1]]");
        successInput("return [[1]] <=> [[2]]");
        successInput("return [[1.0]] <=> [[1.0]]");
        successInput("return [[1.0]] <=> [[2.0]]");
        successInput("return [[1]] <=> [[1.0]]");
        successInput("return [[1]] <=> [[2.0]]");
        successInput("return [[1.0]] <=> [[1]]");
        successInput("return [[1.0]] <=> [[2]]");

        successInput("return [[1]] !<=> [[1]]");
        successInput("return [[1]] !<=> [[2]]");
        successInput("return [[1.0]] !<=> [[1.0]]");
        successInput("return [[1.0]] !<=> [[2.0]]");
        successInput("return [[1]] !<=> [[1.0]]");
        successInput("return [[1]] !<=> [[2.0]]");
        successInput("return [[1.0]] !<=> [[1]]");
        successInput("return [[1.0]] !<=> [[2]]");

        successInput("return [[1]] =? [[1]]");
        successInput("return [[1]] =? [[2]]");
        successInput("return [[1.0]] =? [[1.0]]");
        successInput("return [[1.0]] =? [[2.0]]");
        successInput("return [[1]] =? [[1.0]]");
        successInput("return [[1]] =? [[2.0]]");
        successInput("return [[1.0]] =? [[1]]");
        successInput("return [[1.0]] =? [[2]]");

        successInput("return [[1]] !=? [[1]]");
        successInput("return [[1]] !=? [[2]]");
        successInput("return [[1.0]] !=? [[1.0]]");
        successInput("return [[1.0]] !=? [[2.0]]");
        successInput("return [[1]] !=? [[1.0]]");
        successInput("return [[1]] !=? [[2.0]]");
        successInput("return [[1.0]] !=? [[1]]");
        successInput("return [[1.0]] !=? [[2]]");

        successInput("return [[1]] << [[1]]");
        successInput("return [[1]] << [[2]]");
        successInput("return [[1.0]] << [[1.0]]");
        successInput("return [[1.0]] << [[2.0]]");
        successInput("return [[1]] << [[1.0]]");
        successInput("return [[1]] << [[2.0]]");
        successInput("return [[1.0]] << [[1]]");
        successInput("return [[1.0]] << [[2]]");

        successInput("return [[1]] <? [[1]]");
        successInput("return [[1]] <? [[2]]");
        successInput("return [[1.0]] <? [[1.0]]");
        successInput("return [[1.0]] <? [[2.0]]");
        successInput("return [[1]] <? [[1.0]]");
        successInput("return [[1]] <? [[2.0]]");
        successInput("return [[1.0]] <? [[1]]");
        successInput("return [[1.0]] <? [[2]]");

        successInput("return [[1]] >> [[1]]");
        successInput("return [[1]] >> [[2]]");
        successInput("return [[1.0]] >> [[1.0]]");
        successInput("return [[1.0]] >> [[2.0]]");
        successInput("return [[1]] >> [[1.0]]");
        successInput("return [[1]] >> [[2.0]]");
        successInput("return [[1.0]] >> [[1]]");
        successInput("return [[1.0]] >> [[2]]");

        successInput("return [[1]] >? [[1]]");
        successInput("return [[1]] >? [[2]]");
        successInput("return [[1.0]] >? [[1.0]]");
        successInput("return [[1.0]] >? [[2.0]]");
        successInput("return [[1]] >? [[1.0]]");
        successInput("return [[1]] >? [[2.0]]");
        successInput("return [[1.0]] >? [[1]]");
        successInput("return [[1.0]] >? [[2]]");

        successInput("return [[1]] <<= [[1]]");
        successInput("return [[1]] <<= [[2]]");
        successInput("return [[1.0]] <<= [[1.0]]");
        successInput("return [[1.0]] <<= [[2.0]]");
        successInput("return [[1]] <<= [[1.0]]");
        successInput("return [[1]] <<= [[2.0]]");
        successInput("return [[1.0]] <<= [[1]]");
        successInput("return [[1.0]] <<= [[2]]");

        successInput("return [[1]] <=? [[1]]");
        successInput("return [[1]] <=? [[2]]");
        successInput("return [[1.0]] <=? [[1.0]]");
        successInput("return [[1.0]] <=? [[2.0]]");
        successInput("return [[1]] <=? [[1.0]]");
        successInput("return [[1]] <=? [[2.0]]");
        successInput("return [[1.0]] <=? [[1]]");
        successInput("return [[1.0]] <=? [[2]]");

        successInput("return [[1]] >>= [[1]]");
        successInput("return [[1]] >>= [[2]]");
        successInput("return [[1.0]] >>= [[1.0]]");
        successInput("return [[1.0]] >>= [[2.0]]");
        successInput("return [[1]] >>= [[1.0]]");
        successInput("return [[1]] >>= [[2.0]]");
        successInput("return [[1.0]] >>= [[1]]");
        successInput("return [[1.0]] >>= [[2]]");

        successInput("return [[1]] >=? [[1]]");
        successInput("return [[1]] >=? [[2]]");
        successInput("return [[1.0]] >=? [[1.0]]");
        successInput("return [[1.0]] >=? [[2.0]]");
        successInput("return [[1]] >=? [[1.0]]");
        successInput("return [[1]] >=? [[2.0]]");
        successInput("return [[1.0]] >=? [[1]]");
        successInput("return [[1.0]] >=? [[2]]");

        failureInputWith("return [[1, 2, 3]] == [[1, 2, 3]]", "");
        failureInputWith("return [[1, 2, 3]] < [[1, 2, 3]]", "");
        failureInputWith("return [[1, 2, 3]] > [[1, 2, 3]]", "");
        failureInputWith("return [[1, 2, 3]] != [[1, 2, 3]]", "");

        failureInputWith("return [[1, 2, 3]] >> \"2\"",
            "Attempting to perform arithmetic comparison on non-arraylike type: String");
        failureInputWith("return [[1, 2, 3]] >> 2",
            "Attempting to perform arithmetic comparison on non-arraylike type: Int");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testComparaisonArrayMatrix(){

        successInput("return [1] <=> [[1]]");
        successInput("return [1] <=> [[2]]");
        successInput("return [1.0] <=> [[1.0]]");
        successInput("return [1.0] <=> [[2.0]]");
        successInput("return [1] <=> [[1.0]]");
        successInput("return [1] <=> [[2.0]]");
        successInput("return [1.0] <=> [[1]]");
        successInput("return [1.0] <=> [[2]]");

        successInput("return [1] !<=> [[1]]");
        successInput("return [1] !<=> [[2]]");
        successInput("return [1.0] !<=> [[1.0]]");
        successInput("return [1.0] !<=> [[2.0]]");
        successInput("return [1] !<=> [[1.0]]");
        successInput("return [1] !<=> [[2.0]]");
        successInput("return [1.0] !<=> [[1]]");
        successInput("return [1.0] !<=> [[2]]");

        successInput("return [1] =? [[1]]");
        successInput("return [1] =? [[2]]");
        successInput("return [1.0] =? [[1.0]]");
        successInput("return [1.0] =? [[2.0]]");
        successInput("return [1] =? [[1.0]]");
        successInput("return [1] =? [[2.0]]");
        successInput("return [1.0] =? [[1]]");
        successInput("return [1.0] =? [[2]]");

        successInput("return [1] !=? [[1]]");
        successInput("return [1] !=? [[2]]");
        successInput("return [1.0] !=? [[1.0]]");
        successInput("return [1.0] !=? [[2.0]]");
        successInput("return [1] !=? [[1.0]]");
        successInput("return [1] !=? [[2.0]]");
        successInput("return [1.0] !=? [[1]]");
        successInput("return [1.0] !=? [[2]]");

        successInput("return [1] << [[1]]");
        successInput("return [1] << [[2]]");
        successInput("return [1.0] << [[1.0]]");
        successInput("return [1.0] << [[2.0]]");
        successInput("return [1] << [[1.0]]");
        successInput("return [1] << [[2.0]]");
        successInput("return [1.0] << [[1]]");
        successInput("return [1.0] << [[2]]");

        successInput("return [1] <? [[1]]");
        successInput("return [1] <? [[2]]");
        successInput("return [1.0] <? [[1.0]]");
        successInput("return [1.0] <? [[2.0]]");
        successInput("return [1] <? [[1.0]]");
        successInput("return [1] <? [[2.0]]");
        successInput("return [1.0] <? [[1]]");
        successInput("return [1.0] <? [[2]]");

        successInput("return [1] >> [[1]]");
        successInput("return [1] >> [[2]]");
        successInput("return [1.0] >> [[1.0]]");
        successInput("return [1.0] >> [[2.0]]");
        successInput("return [1] >> [[1.0]]");
        successInput("return [1] >> [[2.0]]");
        successInput("return [1.0] >> [[1]]");
        successInput("return [1.0] >> [[2]]");

        successInput("return [1] >? [[1]]");
        successInput("return [1] >? [[2]]");
        successInput("return [1.0] >? [[1.0]]");
        successInput("return [1.0] >? [[2.0]]");
        successInput("return [1] >? [[1.0]]");
        successInput("return [1] >? [[2.0]]");
        successInput("return [1.0] >? [[1]]");
        successInput("return [1.0] >? [[2]]");

        successInput("return [1] <<= [[1]]");
        successInput("return [1] <<= [[2]]");
        successInput("return [1.0] <<= [[1.0]]");
        successInput("return [1.0] <<= [[2.0]]");
        successInput("return [1] <<= [[1.0]]");
        successInput("return [1] <<= [[2.0]]");
        successInput("return [1.0] <<= [[1]]");
        successInput("return [1.0] <<= [[2]]");

        successInput("return [1] <=? [[1]]");
        successInput("return [1] <=? [[2]]");
        successInput("return [1.0] <=? [[1.0]]");
        successInput("return [1.0] <=? [[2.0]]");
        successInput("return [1] <=? [[1.0]]");
        successInput("return [1] <=? [[2.0]]");
        successInput("return [1.0] <=? [[1]]");
        successInput("return [1.0] <=? [[2]]");

        successInput("return [1] >>= [[1]]");
        successInput("return [1] >>= [[2]]");
        successInput("return [1.0] >>= [[1.0]]");
        successInput("return [1.0] >>= [[2.0]]");
        successInput("return [1] >>= [[1.0]]");
        successInput("return [1] >>= [[2.0]]");
        successInput("return [1.0] >>= [[1]]");
        successInput("return [1.0] >>= [[2]]");

        successInput("return [1] >=? [[1]]");
        successInput("return [1] >=? [[2]]");
        successInput("return [1.0] >=? [[1.0]]");
        successInput("return [1.0] >=? [[2.0]]");
        successInput("return [1] >=? [[1.0]]");
        successInput("return [1] >=? [[2.0]]");
        successInput("return [1.0] >=? [[1]]");
        successInput("return [1.0] >=? [[2]]");

        failureInputWith("return [[1, 2, 3]] == [[1, 2, 3]]", "");
        failureInputWith("return [[1, 2, 3]] < [[1, 2, 3]]", "");
        failureInputWith("return [[1, 2, 3]] > [[1, 2, 3]]", "");
        failureInputWith("return [[1, 2, 3]] != [[1, 2, 3]]", "");
        failureInputWith("return [[1, 2, 3]] == [[1, 2, 3]]", "");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testVarDecl() {
        successInput("var x: Int = 1; return x");
        successInput("var x: Float = 2.0; return x");

        successInput("var x: Int = 0; return x = 3");
        successInput("var x: String = \"0\"; return x = \"S\"");

        successInput("var x: Int[] = [1]; return x");
        successInput("var x: Float[] = [1.0]; return x");
        successInput("var x: String[] = [\"a\"]; return x");

        successInput("var x: Int[] = [1]; return x[0]= 2");
        successInput("var x: Float[] = [1.0]; return x[0] = 2.0");
        successInput("var x: String[] = [\"a\"]; return x[0] = \"S\"");

        successInput("var x: Mat#Int = [[1]]; return x");
        successInput("var x: Mat#Float = [[1.0]]; return x");
        successInput("var x: Mat#String = [[\"a\"]]; return x");

        successInput("var x: Mat#Int = [[1]]; return x[0][0] = 2");
        successInput("var x: Mat#Float = [[1.0]]; return x[0][0] = 2.0");
        successInput("var x: Mat#String = [[\"a\"]]; return x[0][0] = \"S\"");

        successInput("var x: Mat#Int = [1](2, 2); return x");
        successInput("var x: Mat#Float = [1.0](2, 2); return x");
        successInput("var x: Mat#String = [\"a\"](2, 2); return x");

        successInput("var x: Mat#Int = [1](2, 2); return x[0][0] = 2");
        successInput("var x: Mat#Float = [1.0](2, 2); return x[0][0] = 2.0");
        successInput("var x: Mat#String = [\"a\"](2, 2); return x[0][0] = \"S\"");

        failureInputWith("var x: Int = true", "expected Int but got Bool");
        failureInputWith("return x + 1", "Could not resolve: x");
        failureInputWith("return x + 1; var x: Int = 2", "Variable used before declaration: x");

        failureInputWith("var x: Int[] = [2.0]", "");
        failureInputWith("var x: String[] = [2]", "");

        // implicit conversions
        successInput("var x: Float = 1 ; x = 2");
        successInput("var x: Float[] = [2]");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testRootAndBlock () {
        successInput("return");
        successInput("return 1");
        successInput("return 1; return 2");

        successInput("print(\"a\")");
        successInput("print(\"a\" + 1)");
        successInput("print(\"a\"); print(\"b\")");

        successInput("{ print(\"a\"); print(\"b\") }");

        successInput(
            "var x: Int = 1;" +
            "{ print(\"\" + x); var x: Int = 2; print(\"\" + x) }" +
            "print(\"\" + x)");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testCalls() {
        successInput(
            "fun add (a: Int, b: Int): Int { return a + b } " +
                "return add(4, 7)");

        successInput(
            "struct Point { var x: Int; var y: Int }" +
            "return $Point(1, 2)");

        successInput("var str: String = null; return print(str + 1)");

        failureInputWith("return print(1)", "argument 0: expected String but got Int");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testArrayStructAccess() {
        successInput("return [1][0]");
        successInput("return [1.0][0]");
        successInput("return [1, 2][1]");

        successInput("return [[1]][0]");
        successInput("return [[1.0]][0]");
        successInput("return [[1, 2], [3, 4]][1]");

        successInput("return [1, 2, 3][0:1]");
        successInput("return [1.0, 2.0, 3.0][:1]");
        successInput("return [1, 2][:1]");

        successInput("return [[1, 2, 3]][0][0:1]");
        successInput("return [[1.0, 2.0, 3.0]][0][:1]");
        successInput("return [[1, 2]][0][:1]");

        failureInputWith("return [1][true]", "Indexing an array using a non-Int-valued expression");
        failureInputWith("return [1][:true]", "Slicing an array at end using a non-Int-valued expression");
        failureInputWith("return [[1]][true]", "Indexing an array using a non-Int-valued expression");
        failureInputWith("return [[1]][:true]", "Slicing an array at end using a non-Int-valued expression");

        // TODO make this legal?
        // successInput("[].length", 0L);

        successInput("return [1].length");
        successInput("return [1, 2].length");
        successInput("return [[1, 2], [2, 3]].shape");
        successInput("return [[1, 2]].shape");

        successInput("var array: Int[] = null; return array[0]");
        successInput("var array: Int[] = null; return array.length");

        successInput("var x: Int[] = [0, 1]; x[0] = 3; return x[0]");
        successInput("var x: Int[] = []; x[0] = 3; return x[0]");
        successInput("var x: Int[] = null; x[0] = 3");

        successInput("var x: Mat#Int = [[1, 2], [3, 4]]; x[0] = [5, 6]");
        successInput("var x: Mat#Int = [[1, 2], [3, 4]]; x[0][1] = 5; return x[0]");

        successInput(
            "struct P { var x: Int; var y: Int }" +
            "return $P(1, 2).y");

        successInput(
            "struct P { var x: Int; var y: Int }" +
            "var p: P = null;" +
            "return p.y");

        successInput(
            "struct P { var x: Int; var y: Int }" +
            "var p: P = $P(1, 2);" +
            "p.y = 42;" +
            "return p.y");

        successInput(
            "struct P { var x: Int; var y: Int }" +
            "var p: P = null;" +
            "p.y = 42");

        failureInputWith(
            "struct P { var x: Int; var y: Int }" +
            "return $P(1, true)",
            "argument 1: expected Int but got Bool");

        failureInputWith(
            "struct P { var x: Int; var y: Int }" +
            "return $P(1, 2).z",
            "Trying to access missing field z on struct P");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testArrayMatrixSlicing(){

        successInput("return [1, 2, 3, 4, 5, 6][:]");
        successInput("return [1, 2, 3, 4, 5, 6][:2]");
        successInput("return [1, 2, 3, 4, 5, 6][1:]");
        successInput("return [1, 2, 3, 4, 5, 6][1:2]");

        successInput("return [[1, 2, 3], [4, 5, 6], [7, 8, 9]][:]");
        successInput("return [[1, 2, 3], [4, 5, 6], [7, 8, 9]][:2]");
        successInput("return [[1, 2, 3], [4, 5, 6], [7, 8, 9]][1:]");
        successInput("return [[1, 2, 3], [4, 5, 6], [7, 8, 9]][1:2]");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testIfWhile () {
        successInput("if (true) return 1 else return 2");
        successInput("if (false) return 1 else return 2");
        successInput("if (false) return 1 else if (true) return 2 else return 3 ");
        successInput("if (false) return 1 else if (false) return 2 else return 3 ");

        successInput("var i: Int = 0; while (i < 3) { print(\"\" + i); i = i + 1 } ");

        failureInputWith("if 1 return 1",
            "If statement with a non-boolean condition of type: Int");
        failureInputWith("while 1 return 1",
            "While statement with a non-boolean condition of type: Int");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testInference() {
        successInput("var array: Int[] = []");
        successInput("var array: String[] = []");
        successInput("fun use_array (array: Int[]) {} ; use_array([])");
        //FIXME is this inference ?
//        successInput("var matrix: Mat#Int = [[1]]");
//        successInput("var matrix: Mat#String = [[\"Hello\"]]");

    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testTypeAsValues() {
        successInput("struct S{} ; return \"\"+ S");
        successInput("struct S{} ; var type: Type = S ; return \"\"+ type");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testUnconditionalReturn()
    {
        successInput("fun f(): Int { if (true) return 1 else return 2 } ; return f()");

        // TODO: would be nice if this pinpointed the if-statement as missing the return,
        //   not the whole function declaration
        failureInputWith("fun f(): Int { if (true) return 1 } ; return f()",
            "Missing return in function");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testvectorizedFunction()
    {

        successInput(
            "var mat1: Mat#Int = [[1, 2],[3, 4]]" +
                "var mat2: Mat#Int = [[5, 6],[7, 3]]" +
                "fun add (a: Int, b: Int): Int { return a + b } " +
                "return add(mat1, mat2)");

        successInput(
            "var mat1: Mat#Int = [[1, 2],[3, 4]]" +
                "fun add (a: Int, b: Int): Int { return a + b } " +
                "return add(mat1, 7)");

        successInput("fun bigTester (a : Int, b: Int, c: Float): Float {" +
            "    if (a > b && a > c)" +
            "        return a" +
            "    else if (b > a && b > c)" +
            "        return b" +
            "    else" +
            "        return c" +
            "}" +
            "var mat1: Mat#Int = [[6, 7, 8], [0, 0, 0], [-1, -2, -3]]" +
            "var mat2: Mat#Int = [[0, 0, 0], [3, 4, 5], [-1, -2, -3]]" +
            "var mat3: Mat#Int = [[1, 2, 3], [2, 3, 4], [1, 2, 3]]" +
            "print(\"\" + bigTester(mat1, mat2, mat3))");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testCaseStatement() {

        successInput("case \"aaa\" {" +
            "\"b\" : {}," +
            "\"a_a\" : {}," +
            "default : {}" +
            "}");

        successInput("case [1](2, 2) {" +
            "[3](2, 2) : {print(\"coucou\")}," +
            "[_](2, 2) : {var a : Int = 3}" +
            "}");

        successInput("case [1, 2] {" +
            "[3, 4] : {}," +
            "[1] : {}," +
            "default : {}" +
            "}");

        successInput("case 2.5 {" +
            "1.2 : {}," +
            "_ : {}" +
            "}");

        successInput("case 2 {" +
            "1 : {}," +
            "_ : {}" +
            "}");

        failureInputWith("case 2 {" +
            "[1, 2] : {}," +
            "_ : {}" +
            "}", "Cannot compare Int and Int[]");

        failureInputWith("case [1, 2] {" +
            "1 : {}," +
            "_ : {}" +
            "}", "Cannot compare Int[] and Int");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testGenericFunction() {
        successInput(
            "fun test1(x : T) : T {" +
                "return x + 5" +
            "}"
        );

        successInput(
            "fun test2(x : T, y : U) : T {" +
                "var tmp1 : T = x " +
                "var tmp2 : U = y " +
                "return tmp1" +
            "}"
        );

        failureInputWith(
            "fun test2(x : T) : U {" +
                "return 1" +
            "}",
            "Incompatible return type, expected U (Generic) but got Int",
            "Generic return Type should be declared in parameters"
        );
    }


}
