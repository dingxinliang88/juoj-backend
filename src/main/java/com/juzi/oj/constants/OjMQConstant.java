package com.juzi.oj.constants;

/**
 * @author codejuzi
 */
public interface OjMQConstant {

    // region common
    String OJ_EXCHANGE_NAME = "oj_exchange";
    String OJ_QUEUE_NAME = "oj_queue";
    String OJ_ROUTING_KEY = "oj_question_judge";
    String OJ_DIRECT_EXCHANGE = "direct";

    // endregion


    // region dlx
    String OJ_DLX_EXCHANGE = "oj_dlx_exchange";
    String OJ_DLX_QUEUE = "oj_dlx_queue";
    String OJ_DLX_ROUTING_KEY = "oj_dlx_question_judge";

    // endregion

}
