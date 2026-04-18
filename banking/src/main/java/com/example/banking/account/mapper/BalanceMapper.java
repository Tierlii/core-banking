package com.example.banking.account.mapper;

import com.example.banking.account.model.Balance;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
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
    List<Balance> findByAccountId(@Param("accountId") Long accountId);

    @Select("""
            SELECT id, account_id, currency, available_amount, updated_at
            FROM balances
            WHERE account_id = #{accountId}
              AND currency = #{currency}
            """)
    @ResultMap("balanceResultMap")
    Balance findByAccountIdAndCurrency(@Param("accountId") Long accountId,
                                       @Param("currency") String currency);

    @Select("""
            SELECT id, account_id, currency, available_amount, updated_at
            FROM balances
            WHERE account_id = #{accountId}
              AND currency = #{currency}
            FOR UPDATE
            """)
    @ResultMap("balanceResultMap")
    Balance findByAccountIdAndCurrencyForUpdate(@Param("accountId") Long accountId,
                                                @Param("currency") String currency);

    @Update("""
            UPDATE balances
            SET available_amount = #{availableAmount},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    void updateAvailableAmount(@Param("id") Long id,
                               @Param("availableAmount") BigDecimal availableAmount);
}