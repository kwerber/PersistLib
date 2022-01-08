package de.kwerber.persistlib.sql;

import de.kwerber.persistlib.PersistenceContext;
import de.kwerber.persistlib.PersistentAttribute;
import de.kwerber.persistlib.exception.SerializeException;
import de.kwerber.persistlib.handler.ClassHandler;
import de.kwerber.persistlib.query.OrderByClause;
import de.kwerber.persistlib.query.ParameterizedQuery;
import de.kwerber.persistlib.query.PersistenceSelect;
import de.kwerber.persistlib.type.TypeMapper;
import de.kwerber.persistlib.util.Check;
import de.kwerber.persistlib.util.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class MariadbTranslator<T, Id> implements SQLTranslator<T, Id> {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	@Override
	public ParameterizedQuery getInsertQuery(PersistenceContext<T, Id> ctx, T instance) {
		Check.notNull(ctx);
		Check.notNull(instance);

		String sql = "INSERT INTO `<table_name>` (<cols>) VALUES (<vals>);";

		List<PersistentAttribute> attributes = ctx.getHandler().getAttributes().stream()
			.filter(a -> !a.isAutoIncremented()) // let auto incr columns be auto incremented
			.collect(Collectors.toList());

		String cols = attributes.stream()
			.map(PersistentAttribute::getId)
			.map(id -> "`" + id + "`")
			.collect(Collectors.joining(", "));

		String vals = Utils.generateParameters(attributes.size());

		sql = sql.replace("<table_name>", ctx.getTableName())
			.replace("<cols>", cols)
			.replace("<vals>", vals);

		Object[] params = Utils.collectQueryParams(ctx, attributes, instance);

		return new ParameterizedQuery(sql, params);
	}

	@Override
	public ParameterizedQuery getUpdateQuery(PersistenceContext<T, Id> ctx, T instance) {
		Check.notNull(ctx);
		Check.notNull(instance);

		ClassHandler<T, Id> handler = ctx.getHandler();
		TypeMapper typeMapper = ctx.getTypeMapper();

		PersistentAttribute primaryAttr = handler.getPrimaryAttribute();
		Id primaryValue = handler.getPrimaryValue(instance);

		if (!typeMapper.applies(primaryValue.getClass())) {
			throw new SerializeException("Cannot serialize primary key: " + primaryValue.getClass());
		}

		String sql = "UPDATE `<table_name>` SET <vals> WHERE `<id_col>` = ?;"
			.replace("<table_name>", ctx.getTableName())
			.replace("<id_col>", primaryAttr.getId());

		List<PersistentAttribute> attributes = ctx.getHandler().getAttributes();

		String vals = attributes.stream()
			.map(a -> "`<col_name>` = ?".replace("<col_name>", a.getId()))
			.collect(Collectors.joining(", "));

		sql = sql.replace("<vals>", vals);

		Object[] params = Utils.collectQueryParams(ctx, attributes, instance);
		params = Utils.append(params, typeMapper.serialize(primaryValue));

		return new ParameterizedQuery(sql, params);
	}

	@Override
	public ParameterizedQuery getDeleteQuery(PersistenceContext<T, Id> ctx, Id id) {
		Check.notNull(ctx);
		Check.notNull(id);

		String sql = "DELETE FROM `<table_name>` WHERE `<id_col>` = ?;"
			.replace("<table_name>", ctx.getTableName())
			.replace("<id_col>", ctx.getHandler().getPrimaryAttribute().getId());

		if (!ctx.getTypeMapper().applies(id.getClass())) {
			throw new SerializeException("cannot serialize primary key: " + id.getClass());
		}

		Object idSerialized = ctx.getTypeMapper().serialize(id);

		return new ParameterizedQuery(sql, new Object[] { idSerialized });
	}

	@Override
	public ParameterizedQuery getCountQuery(PersistenceContext<T, Id> ctx, PersistenceSelect<T, Id> query) {
		return buildSelect(ctx, query, "COUNT(*)");
	}

	@Override
	public ParameterizedQuery getSelectQuery(PersistenceContext<T, Id> ctx, PersistenceSelect<T, Id> query) {
		return buildSelect(ctx, query, "*");
	}

	@Override
	public ParameterizedQuery getDeleteAllQuery(PersistenceContext<T, Id> ctx) {
		Check.notNull(ctx);

		String sql = "DELETE FROM `<table_name>`".replace("<table_name>", ctx.getTableName());
		return new ParameterizedQuery(sql);
	}

	private ParameterizedQuery buildSelect(PersistenceContext<T, Id> ctx, PersistenceSelect<T, Id> query, String column) {
		Check.notNull(ctx);
		Check.notNull(query);
		Check.notNullNotEmpty(column);

		String sql = "SELECT " + column + " FROM `<table_name>`"
			.replace("<table_name>", ctx.getTableName());

		Object[] params;

		if (query.getWhereClause().isPresent()) {
			Object[] args = query.getWhereClause().get().getArgs();
			params = new Object[args.length];

			TypeMapper typeMapper = ctx.getTypeMapper();

			for (int i = 0; i < params.length; i++) {
				if (args[i] == null) { continue; }

				if (typeMapper.applies(args[i].getClass())) {
					params[i] = typeMapper.serialize(args[i]);
				}
				else {
					throw new SerializeException("Cannot serialize param " + i + ": " + params[i].getClass() + " (" + params[i] + ")");
				}
			}

			sql += " WHERE " + query.getWhereClause().get().getClause();
		}
		else {
			params = EMPTY_ARRAY;
		}

		if (query.getOrderByClause().isPresent()) {
			OrderByClause clause = query.getOrderByClause().get();
			sql += "ORDER BY " + clause.getColumn() + " " + clause.getOrder();
		}

		if (query.getLimit().isPresent()) {
			sql += " LIMIT " + query.getLimit().get();
		}

		sql = sql + ";";

		return new ParameterizedQuery(sql, params);
	}

}
