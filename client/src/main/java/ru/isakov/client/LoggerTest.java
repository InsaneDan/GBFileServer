package ru.isakov.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

    public static void main(String[] args) {
        logger.trace("color test");
        logger.debug("color test");
        logger.info("color test");
        logger.warn("color test");
        logger.error("color test");
    }
}
