import env from "#start/env";
import logger from "@adonisjs/core/services/logger";
import mongoose from "mongoose";
import { prettyPrintError } from "@adonisjs/core";

var maxRetries = 5;
var retryCounter = 0;

export const connectToDatabase = async (): Promise<boolean> => {
    let connectionString = env.get("DB_CONNECTION_STRING", 'null') as string;   
    if (connectionString === 'null') {
        logger.error("No connection string detected!");
        return false;
    }
    
    try {
        await mongoose.connect(connectionString, {});
    } catch (err) {
        logger.error("A critical error has occurred: %s", err);
        prettyPrintError(err);
        process.exitCode = -1;
        return false;
    }

    logger.info("Connected to the database!");
    return true;
}

export const checkState = async (): Promise<void> => {
    let state = mongoose.connection.readyState;
    
    switch (state) {
        case 3:
        await checkForDisconnect();
        logger.info('Client disconnected! Have to reconnect!');
        await connectToDatabase();
        return;
        case 0:
        await checkForDisconnect();
        logger.info('Client disconnected! Have to reconnect!');
        await connectToDatabase();
        return;
    }
};

const checkForDisconnect = async () => {
    if (retryCounter >= maxRetries) {
        mongoose.connection.close();
        logger.error("Max retries reached! Exiting!");
        process.exitCode = -1;
        process.exit();
    }
    retryCounter++;
    return;
};