package com.example.banking.account.mapper;

import com.example.banking.account.model.Account;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AccountMapper {

    @Insert("""
            INSERT INTO accounts (customer_id, country)
            VALUES (#{customerId}, #{country})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Account account);

    @Select("""
            SELECT id, customer_id, country, created_at
            FROM accounts
            WHERE id = #{id}
            """)
    @Results(id = "accountResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "country", column = "country"),
            @Result(property = "createdAt", column = "created_at")
    })
    Account findById(@Param("id") Long id);

    @Select("""
            SELECT EXISTS(
                SELECT 1
                FROM accounts
                WHERE id = #{id}
            )
            """)
    boolean existsById(@Param("id") Long id);
}