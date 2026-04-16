package com.example.banking.transaction.mapper;

import com.example.banking.transaction.model.Transaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransactionMapper {

    @Insert("""
            INSERT INTO transactions (
                account_id,
                amount,
                currency,
                direction,
                description,
                balance_after_transaction
            )
            VALUES (
                #{accountId},
                #{amount},
                #{currency},
                #{direction},
                #{description},
                #{balanceAfterTransaction}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Transaction transaction);

    @Select("""
            SELECT id,
                   account_id,
                   amount,
                   currency,
                   direction,
                   description,
                   balance_after_transaction,
                   created_at
            FROM transactions
            WHERE account_id = #{accountId}
            ORDER BY created_at DESC
            """)
    @Results(id = "transactionResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "accountId", column = "account_id"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "currency", column = "currency"),
            @Result(property = "direction", column = "direction"),
            @Result(property = "description", column = "description"),
            @Result(property = "balanceAfterTransaction", column = "balance_after_transaction"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
}