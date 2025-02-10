import app from "@adonisjs/core/services/app";
import logger from "@adonisjs/core/services/logger";
import mongoose from 'mongoose';
import { prettyPrintError } from "@adonisjs/core";
import env from '#start/env';
import { pictureModel } from "../Models/pictureCollection.js";
import { HttpContext } from "@adonisjs/core/http";

app.ready(() => {
  logger.info("PictureProvider ready!");
  connectToDatabase();
});

type Response = {
  status: boolean,
  data: any
};

const checkState = async (): Promise<void> => {
  let state = mongoose.connection.readyState;

  switch (state) {
    case 3:
      logger.info('Client disconnected! Have to reconnect!');
      await connectToDatabase();
      return;
    case 0:
      logger.info('Client disconnected! Have to reconnect!');
      await connectToDatabase();
      return;
  }
};

const connectToDatabase = async (): Promise<boolean> => {
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
  return true;
};

/**
 * Save a picture to the database
 * @param record - The picture record to save
 * @returns The response object containing the status and data
 */
export const savePicture = async (record: Record<string, string>): Promise<Response> => {
  await checkState();

  if (Object.keys(record).length !== 2) {
    logger.info("Parameters didn't work!");
    return { status: false, data: undefined };
  }

  let savedPicture;

  try {
    const schema = new pictureModel({
      picture: record["picture"],
      diagnosis: record["diagnosis"]
    });

    savedPicture = await schema.save();
  } catch (ex) {
    logger.error("Error occurred: %s", ex);
    prettyPrintError(ex);
    return { status: false, data: savedPicture };
  }

  return { status: true, data: savedPicture };
};

/**
 * Find pictures from the database
 * @param ctx - The HTTP context
 * @returns The response object containing the pictures
 */
export const findPictures = async (ctx: HttpContext) => {
  let queryParams = ctx.request.qs();
  let page = Number(queryParams.page) || 1;
  let limit = Number(queryParams.limit) || 10;

  const options = {
    page: page,
    limit: limit,
    collation: {
      locale: 'en',
    },
  };

  try {
    let data = await pictureModel.paginate({}, options);
    return ctx.response.status(200).json(data);
  } catch (error) {
    console.error(error);
    return ctx.response.status(500).json({ message: 'An error occurred' });
  }
};