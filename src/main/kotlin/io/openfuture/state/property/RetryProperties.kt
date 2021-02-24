package io.openfuture.state.property

class RetryProperties {

    /*
     * Progressive attempts count, delay  between attempts
     * calculated by arithmetic progression
     * (used Fibonachi row)
     */
    val progressiveMaxAttempts: Int = 10

    /*
     * Daily attempts count, used after progressive
     * attempts count is reached
     */
    val dailyMaxAttempts: Int = 17
}
