package norswap.sigh;

import norswap.autumn.Grammar;
import norswap.sigh.ast.*;

import static norswap.sigh.ast.UnaryOperator.NOT;

@SuppressWarnings("Convert2MethodRef")
public class SighGrammar extends Grammar
{
    // ==== LEXICAL ===========================================================

    public rule line_comment =
        seq("//", seq(not("\n"), any).at_least(0));

    public rule multiline_comment =
        seq("/*", seq(not("*/"), any).at_least(0), "*/");

    public rule ws_item = choice(
        set(" \t\n\r;"),
        line_comment,
        multiline_comment);

    {
        ws = ws_item.at_least(0);
        id_part = choice(alphanum, '_');
    }

    public rule STAR                    = word("*");
    public rule SLASH                   = word("/");
    public rule PERCENT                 = word("%");
    public rule PLUS                    = word("+");
    public rule MINUS                   = word("-");
    public rule LBRACE                  = word("{");
    public rule RBRACE                  = word("}");
    public rule LPAREN                  = word("(");
    public rule RPAREN                  = word(")");
    public rule LSQUARE                 = word("[");
    public rule RSQUARE                 = word("]");
    public rule COLON                   = word(":");
    public rule EQUALS_EQUALS           = word("==");
    public rule EQUALS_QMARK            = word("=?");
    public rule BANG_EQUALS_QMARK       = word("!=?");
    public rule LANGLE_EQUALS_RANGLE    = word("<=>");
    public rule BANG_LANGLE_EQUALS_RANGLE = word("!<=>");
    public rule EQUALS                  = word("=");
    public rule BANG_EQUAL              = word("!=");
    public rule LANGLE_EQUAL            = word("<=");
    public rule LANGLE_EQUAL_QMARK      = word("<=?");
    public rule LANGLE_LANGLE_EQUAL     = word("<<=");
    public rule RANGLE_EQUAL            = word(">=");
    public rule RANGLE_EQUAL_QMARK      = word(">=?");
    public rule RANGLE_RANGLE_EQUAL     = word(">>=");
    public rule LANGLE                  = word("<");
    public rule LANGLE_LANGLE           = word("<<");
    public rule LANGLE_QMARK            = word("<?");
    public rule RANGLE                  = word(">");
    public rule RANGLE_RANGLE           = word(">>");
    public rule RANGLE_QMARK            = word(">?");
    public rule AMP_AMP                 = word("&&");
    public rule BAR_BAR                 = word("||");
    public rule BANG                    = word("!");
    public rule DOT                     = word(".");
    public rule DOLLAR                  = word("$");
    public rule COMMA                   = word(",");
    public rule HASHTAG                 = word("#");
    public rule AT                      = word("@");
//    public rule UNDERSCORE              = word("_");

    public rule _var            = reserved("var");
    public rule _fun            = reserved("fun");
    public rule _struct         = reserved("struct");
    public rule _if             = reserved("if");
    public rule _else           = reserved("else");
    public rule _case           = reserved("case");
    public rule _default           = reserved("default");
    public rule _while          = reserved("while");
    public rule _return         = reserved("return");
    public rule _mat            = reserved("Mat");

    public rule number =
        seq(opt('-'), choice('0', digit.at_least(1)));

    public rule integer =
        number
        .push($ -> new IntLiteralNode($.span(), Long.parseLong($.str())))
        .word();

    public rule floating =
        seq(number, '.', digit.at_least(1))
        .push($ -> new FloatLiteralNode($.span(), Double.parseDouble($.str())))
        .word();

    public rule string_char = choice(
        seq(set('"', '\\').not(), any),
        seq('\\', set("\\nrt")));

    public rule string_content =
        string_char.at_least(0)
        .push($ -> $.str());

    public rule string =
        seq('"', string_content, '"')
        .push($ -> new StringLiteralNode($.span(), $.$[0]))
        .word();

    public rule identifier =
        identifier(seq(choice(alpha, '_'), id_part.at_least(0)))
        .push($ -> $.str());
    
    // ==== SYNTACTIC =========================================================
    
    public rule reference =
        identifier
        .push($ -> new ReferenceNode($.span(), $.$[0]));

    public rule constructor =
        seq(DOLLAR, reference)
        .push($ -> new ConstructorNode($.span(), $.$[0]));
    
    public rule simple_type =
        identifier
        .push($ -> new SimpleTypeNode($.span(), $.$[0]));

    public rule paren_expression = lazy(() ->
        seq(LPAREN, this.expression, RPAREN)
        .push($ -> new ParenthesizedNode($.span(), $.$[0])));

    public rule expressions = lazy(() ->
        this.expression.sep(0, COMMA)
        .as_list(ExpressionNode.class));

    public rule array =
        seq(LSQUARE, expressions, RSQUARE)
            .push($ -> new ArrayLiteralNode($.span(), $.$[0]));

    public rule matrix_expressions = lazy(() ->
        this.array.sep(1, COMMA)
            .as_list(ArrayLiteralNode.class));

    public rule matrix =
        seq(LSQUARE, matrix_expressions, RSQUARE) //[[], [], []]
            .push($ -> new MatrixLiteralNode($.span(), $.$[0]));

    public rule matrix_generator = lazy(() ->
        seq(LSQUARE, this.expression, RSQUARE,
            LPAREN, this.expression.sep(1, COMMA).as_list(ExpressionNode.class), RPAREN)
        .push($ -> new MatrixGeneratorNode($.span(), $.$[0], $.$[1])));

    public rule basic_expression = choice(
        constructor,
        reference,
        floating,
        integer,
        string,
        paren_expression,
        matrix_generator,
        matrix,
        array);

    public rule function_args =
        seq(LPAREN, expressions, RPAREN);

    public rule suffix_expression = left_expression()
        .left(basic_expression)
        .suffix(seq(DOT, identifier),
            $ -> new FieldAccessNode($.span(), $.$[0], $.$[1]))
        .suffix(seq(LSQUARE, lazy(() -> this.expression), RSQUARE),
            $ -> new ArrayAccessNode($.span(), $.$[0], $.$[1]))
        .suffix(seq(LSQUARE, COLON, lazy(() -> this.expression).opt(), RSQUARE),  // arr[:(y)]
            $ -> {
                if ($.$.length < 2)
                    return new SlicingAccessNode($.span(), $.$[0]);
                else
                    return new SlicingAccessNode($.span(), $.$[0], null, $.$[1]);
            })
        .suffix(seq(LSQUARE, lazy(() -> this.expression), COLON, lazy(() -> this.expression).opt(), RSQUARE),  // arr[x:(y)]
            $ -> {
                if ($.$.length < 3)
                    return new SlicingAccessNode($.span(), $.$[0], $.$[1]);
                else
                    return new SlicingAccessNode($.span(), $.$[0], $.$[1], $.$[2]);
            })
        .suffix(function_args,
            $ -> new FunCallNode($.span(), $.$[0], $.$[1]));

    public rule prefix_expression = right_expression()
        .operand(suffix_expression)
        .prefix(BANG.as_val(NOT),
            $ -> new UnaryExpressionNode($.span(), $.$[0], $.$[1]));

    public rule mult_op = choice(
        STAR        .as_val(BinaryOperator.MULTIPLY),
        SLASH       .as_val(BinaryOperator.DIVIDE),
        PERCENT     .as_val(BinaryOperator.REMAINDER),
        AT          .as_val(BinaryOperator.DOT_PRODUCT));

    public rule add_op = choice(
        PLUS        .as_val(BinaryOperator.ADD),
        MINUS       .as_val(BinaryOperator.SUBTRACT));

    public rule cmp_op = choice(
        EQUALS_QMARK            .as_val(BinaryOperator.M_ONE_EQUAL),
        BANG_EQUALS_QMARK       .as_val(BinaryOperator.M_ONE_NOT_EQUAL),
        LANGLE_EQUALS_RANGLE    .as_val(BinaryOperator.M_ALL_EQUAL),
        BANG_LANGLE_EQUALS_RANGLE    .as_val(BinaryOperator.M_ALL_NOT_EQUAL),
        LANGLE_LANGLE_EQUAL     .as_val(BinaryOperator.M_ALL_LOWER_EQUAL),
        LANGLE_LANGLE           .as_val(BinaryOperator.M_ALL_LOWER),
        LANGLE_EQUAL_QMARK      .as_val(BinaryOperator.M_ONE_LOWER_EQUAL),
        LANGLE_QMARK            .as_val(BinaryOperator.M_ONE_LOWER),
        RANGLE_RANGLE_EQUAL     .as_val(BinaryOperator.M_ALL_GREATER_EQUAL),
        RANGLE_RANGLE           .as_val(BinaryOperator.M_ALL_GREATER),
        RANGLE_QMARK            .as_val(BinaryOperator.M_ONE_GREATER),
        RANGLE_EQUAL_QMARK      .as_val(BinaryOperator.M_ONE_GREATER_EQUAL),

        EQUALS_EQUALS.as_val(BinaryOperator.EQUALITY),
        BANG_EQUAL  .as_val(BinaryOperator.NOT_EQUALS),
        LANGLE_EQUAL.as_val(BinaryOperator.LOWER_EQUAL),
        RANGLE_EQUAL.as_val(BinaryOperator.GREATER_EQUAL),
        LANGLE      .as_val(BinaryOperator.LOWER),
        RANGLE      .as_val(BinaryOperator.GREATER)
);

    public rule mult_expr = left_expression()
        .operand(prefix_expression)
        .infix(mult_op,
            $ -> new BinaryExpressionNode($.span(), $.$[0], $.$[1], $.$[2]));

    public rule add_expr = left_expression()
        .operand(mult_expr)
        .infix(add_op,
            $ -> new BinaryExpressionNode($.span(), $.$[0], $.$[1], $.$[2]));

    public rule order_expr = left_expression()
        .operand(add_expr)
        .infix(cmp_op,
            $ -> new BinaryExpressionNode($.span(), $.$[0], $.$[1], $.$[2]));

    public rule and_expression = left_expression()
        .operand(order_expr)
        .infix(AMP_AMP.as_val(BinaryOperator.AND),
            $ -> new BinaryExpressionNode($.span(), $.$[0], $.$[1], $.$[2]));

    public rule or_expression = left_expression()
        .operand(and_expression)
        .infix(BAR_BAR.as_val(BinaryOperator.OR),
            $ -> new BinaryExpressionNode($.span(), $.$[0], $.$[1], $.$[2]));

    public rule assignment_expression = right_expression() // var a : Mat#Int = [1](3, 3)
        .operand(or_expression)
        .infix(EQUALS,
            $ -> new AssignmentNode($.span(), $.$[0], $.$[1]));

    public rule expression =
        seq(assignment_expression);
        // TODO : slicing expression

    public rule expression_stmt =
        expression
        .filter($ -> {
            if (!($.$[0] instanceof AssignmentNode || $.$[0] instanceof FunCallNode))
                return false;
            $.push(new ExpressionStatementNode($.span(), $.$[0]));
            return true;
        });

    public rule array_type = left_expression()
        .left(simple_type)
        .suffix(seq(LSQUARE, RSQUARE),
            $ -> new ArrayTypeNode($.span(), $.$[0]));

    public rule matrix_type = lazy(() -> seq(_mat, HASHTAG, this.type)
        .push($ -> new MatrixTypeNode($.span(), $.$[0])));

    public rule type = choice(
        seq(array_type),
        matrix_type);

    public rule statement = lazy(() -> choice(
        this.block,
        this.var_decl,
        this.fun_decl,
        this.struct_decl,
        this.if_stmt,
        this.while_stmt,
        this.case_stmt,
        this.return_stmt,
        this.expression_stmt));

    public rule statements =
        statement.at_least(0)
        .as_list(StatementNode.class);

    public rule block =
        seq(LBRACE, statements, RBRACE)
        .push($ -> new BlockNode($.span(), $.$[0]));

    public rule var_decl =
        seq(_var, identifier, COLON, type, EQUALS, expression)
        .push($ -> new VarDeclarationNode($.span(), $.$[0], $.$[1], $.$[2]));

    public rule parameter =
        seq(identifier, COLON, type)
        .push($ -> new ParameterNode($.span(), $.$[0], $.$[1]));

    public rule parameters =
        parameter.sep(0, COMMA)
        .as_list(ParameterNode.class);

    public rule maybe_return_type =
        seq(COLON, type).or_push_null();

    public rule fun_decl =
        seq(_fun, identifier, LPAREN, parameters, RPAREN, maybe_return_type, block)
        .push($ -> new FunDeclarationNode($.span(), $.$[0], $.$[1], $.$[2], $.$[3]));

    public rule field_decl =
        seq(_var, identifier, COLON, type)
        .push($ -> new FieldDeclarationNode($.span(), $.$[0], $.$[1]));

    public rule struct_body =
        seq(LBRACE, field_decl.at_least(0).as_list(DeclarationNode.class), RBRACE);

    public rule struct_decl =
        seq(_struct, identifier, struct_body)
        .push($ -> new StructDeclarationNode($.span(), $.$[0], $.$[1]));

    public rule if_stmt =
        seq(_if, expression, statement, seq(_else, statement).or_push_null())
        .push($ -> new IfNode($.span(), $.$[0], $.$[1], $.$[2]));

    public rule case_body =
        seq(expression, COLON, block) //LSQUARE, choice(UNDERSCORE.as_val(MATCH_ELEM), expression).sep(0, COMMA).as_list(null), RSQUARE
            .push($ -> new CaseBodyNode($.span(), $.$[0], $.$[1]));

    public rule case_stmt =
        seq(_case, expression, LBRACE, case_body.sep(1, COMMA).as_list(CaseBodyNode.class), seq(COMMA, _default, COLON, block).or_push_null(), RBRACE)
            .push($ -> new CaseNode($.span(), $.$[0], $.$[1], $.$[2]));

    public rule while_stmt =
        seq(_while, expression, statement)
        .push($ -> new WhileNode($.span(), $.$[0], $.$[1]));

    public rule return_stmt =
        seq(_return, expression.or_push_null())
        .push($ -> new ReturnNode($.span(), $.$[0]));

    public rule root =
        seq(ws, statement.at_least(1))
        .as_list(StatementNode.class)
        .push($ -> new RootNode($.span(), $.$[0]));

    @Override public rule root () {
        return root;
    }
}
