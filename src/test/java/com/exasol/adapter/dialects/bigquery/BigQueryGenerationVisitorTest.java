package com.exasol.adapter.dialects.bigquery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.SqlDialectFactory;
import com.exasol.adapter.dialects.rewriting.SqlGenerationContext;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.adapter.sql.*;

@ExtendWith(MockitoExtension.class)
class BigQueryGenerationVisitorTest {
    private SqlNodeVisitor<String> visitor;

    @BeforeEach
    void beforeEach(@Mock final ConnectionFactory connectionFactoryMock) {
        final SqlDialectFactory dialectFactory = new BigQuerySqlDialectFactory();
        final SqlDialect dialect = dialectFactory.createSqlDialect(connectionFactoryMock,
                AdapterProperties.emptyProperties());
        final SqlGenerationContext context = new SqlGenerationContext("test_catalog", "test_schema", false);
        this.visitor = new BigQueryGenerationVisitor(dialect, context);
    }

    @ParameterizedTest
    @CsvSource({ "BIT_LSHIFT, <<", "BIT_RSHIFT, >>" })
    void testRewriteDivFunction(final String functionName, final String expectedOperator) throws AdapterException {
        final List<SqlNode> arguments = List.of(new SqlLiteralExactnumeric(BigDecimal.TEN),
                new SqlLiteralExactnumeric(BigDecimal.ONE));
        final SqlFunctionScalar sqlFunctionScalar = new SqlFunctionScalar(ScalarFunction.valueOf(functionName),
                arguments);
        assertThat(this.visitor.visit(sqlFunctionScalar), equalTo("10 " + expectedOperator + " 1"));
    }
}