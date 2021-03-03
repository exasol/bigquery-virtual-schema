package com.exasol.adapter.dialects.bigquery;

import java.util.ArrayList;
import java.util.List;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.rewriting.SqlGenerationContext;
import com.exasol.adapter.dialects.rewriting.SqlGenerationVisitor;
import com.exasol.adapter.sql.*;

/**
 * This class generates SQL queries for the {@link BigQuerySqlDialect}.
 */
public class BigQueryGenerationVisitor extends SqlGenerationVisitor {
    /**
     * Create a new instance of the {@link BigQueryGenerationVisitor}.
     *
     * @param dialect {@link BigQuerySqlDialect} SQL dialect
     * @param context SQL generation context
     */
    public BigQueryGenerationVisitor(final SqlDialect dialect, final SqlGenerationContext context) {
        super(dialect, context);
    }

    @Override
    public String visit(final SqlFunctionScalar function) throws AdapterException {
        if (function.getFunction() == ScalarFunction.BIT_LSHIFT) {
            return generateBinaryFunction(function, "<<");
        } else if (function.getFunction() == ScalarFunction.BIT_RSHIFT) {
            return generateBinaryFunction(function, ">>");
        } else {
            return super.visit(function);
        }
    }

    private String generateBinaryFunction(final SqlFunctionScalar function, final String operator)
            throws AdapterException {
        final List<SqlNode> arguments = function.getArguments();
        final List<String> argumentsSql = new ArrayList<>(arguments.size());
        for (final SqlNode node : arguments) {
            argumentsSql.add(node.accept(this));
        }
        return argumentsSql.get(0) + " " + operator + " " + argumentsSql.get(1);
    }
}