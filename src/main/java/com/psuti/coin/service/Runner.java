package com.psuti.coin.service;


import com.psuti.coin.model.Block;
import com.psuti.coin.model.BlockChain;
import com.psuti.coin.model.Transaction;
import com.psuti.coin.util.ResultHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Runner {

    private BlockChain blockChain = BlockChain.getInstance();

    private static final int MIN_TRANSACTION_IN_BLOCK = 1;

    private static boolean isWork = true;

    private static final int DELAY_TIME = 150;

    private static final int MAX_ATTEMPTS = 3;

    private static int counter;

    public static void setIsWork(boolean isWork) {
        Runner.isWork = isWork;
    }

    private Logger logger = LogManager.getLogger(Runner.class);

    @Async
    public void run() {
        logger.log(Level.INFO, "Starting runner");
        while(isWork) {
            if(blockChain.getTransactionQueue().size() >= MIN_TRANSACTION_IN_BLOCK) {
                Block block = new Block(blockChain.getLastBlock().getHash(), ResultHelper.getCurrentTime());
                Transaction currentTx;
                while(!blockChain.getTransactionQueue().isEmpty()) {
                    currentTx = blockChain.getTransactionQueue().poll();
                    block.getTransactions().add(currentTx);
/*                    if(block.addTransaction(currentTx)) {
                        blockChain.getTransactionQueue().remove(currentTx);
                    } else {
                        counter++;
                        if(counter >= MAX_ATTEMPTS) {
                            blockChain.getTransactionQueue().remove(currentTx);
                        }
                        break;
                    }*/
                }
                if(!block.getTransactions().isEmpty()) {
                    block.mineBlock();
                    blockChain.addBlock(block);
                }
            }
            try {
                Thread.sleep(DELAY_TIME);
            } catch (Exception ignored) {}
        }
    }
}
