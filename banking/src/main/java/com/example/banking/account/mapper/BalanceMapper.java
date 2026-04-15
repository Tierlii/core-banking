package com.example.banking.account.mapper;

import com.example.banking.account.model.Balance;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BalanceMapper {

    @Insert("""
            INSERT INTO balances (account_id, currency, available_amount)
            VALUES (#{accountId}, #{currency}, #{availableAmount})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Balance balance);

    @Select("""
            SELECT id, account_id, currency, available_amount, updated_at
            FROM balances
            WHERE account_id = #{accountId}
            ORDER BY currency
            """)
    @Results(id = "balanceResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "currency", column = "currency"),
            @Result(property = "availableAmount", column = "available_amount"),
            @Result(property = "updatedAt", column = "updated_at")
    })
    List<Balance> findByAccountId(Long accountId);

    @Select("""
            SELECT id, account_id, currency, available_amount, updated_at
            FROM balances
            WHERE account_id = #{accountId}
              AND currency = #{currency}
            """)
    @ResultMap("balanceResultMap")
    Balance findByAccountIdAndCurrency(@Param("accountId") Long accountId,
                                       @Param("currency") String currency);
}