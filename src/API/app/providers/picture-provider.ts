import logger from "@adonisjs/core/services/logger";
import { prettyPrintError } from "@adonisjs/core";
import { pictureModel } from "../Models/pictureCollection.js";
import { HttpContext } from "@adonisjs/core/http";
import { checkState } from "../utils/db-funcs.js";

type Response = {
  status: boolean,
  data: any
};

export class PictureProvider {
  private static instance: PictureProvider;
  private ammount: Number = 0;

  private constructor() {
    logger.info("Picture Provider created!");
    pictureModel.countDocuments().then((item) => {this.ammount = item})
  }

  public static async getInstance(): Promise<PictureProvider> {
    if (!PictureProvider.instance) {
      PictureProvider.instance = new PictureProvider();
    }

    return PictureProvider.instance;
  }

  public savePicture = async (record: Record<string, string>): Promise<Response> => {
    await checkState();
    if (Object.keys(record).length !== 2) {
      logger.info("Parameters didn't work!");
      return { status: false, data: undefined };
    }

    let savedPicture;

    try {
      savedPicture = await pictureModel.create(record);
    } catch (err) {
      logger.error("An error occurred: %s", err);
      prettyPrintError(err);
      return { status: false, data: undefined };
    }

    return { status: true, data: savedPicture };
  }

  public findPictures = async (ctx: HttpContext) => {
    await checkState();
    let queryParams = ctx.request.qs();

    const options = {
      page: Number(queryParams.page) || 1,
      limit: Number(queryParams.limit) || 10,
      collation: {
        locale: 'en',
      },
    };

    try {
      return ctx.response.status(200).json(await pictureModel.paginate({}, options));
    } catch (error) {
      console.error(error);
      return ctx.response.status(500).json({ message: 'An error occurred' });
    }
  }

  /**
   * Get the count of all pictures
   * @param ctx - The HTTP context
   * @returns The response object containing the count of all pictures
   */
  public getCount = async (ctx: HttpContext) => {
    await checkState();

    try {
      const count = await pictureModel.estimatedDocumentCount();
      logger.info(`Total pictures: ${count}`);
      return ctx.response.status(200).json({ count });
    } catch (error) {
      logger.error("An error occurred: %s", error);
      prettyPrintError(error);
      logger.info("Request failed while getting the Count of pages!");
      return ctx.response.status(500).json({ message: 'An error occurred' });
    }
  }
  
  /**
   * Get all unique diagnoses
   * @param ctx - The HTTP context
   * @returns The response object containing all unique diagnoses
   */
  public getLabels = async (ctx: HttpContext) => {
    await checkState();

    try {
      const diagnoses = await pictureModel.distinct('diagnosis');
      return ctx.response.status(200).json({ diagnoses });
    } catch (error) {
      logger.error("An error occurred: %s", error);
      prettyPrintError(error);
      return ctx.response.status(500).json({ message: 'An error occurred' });
    }
  }
}