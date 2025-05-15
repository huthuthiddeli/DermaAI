import logger from '@adonisjs/core/services/logger'
import { prettyPrintError } from '@adonisjs/core'
import { pictureModel } from '#models/pictureCollection'
import { HttpContext } from '@adonisjs/core/http'
import { checkState } from './db_func_service.js'
import { inject } from '@adonisjs/core'
import cache from '@adonisjs/cache/services/main'

type Response = {
  status: boolean
  data: any
}

@inject()
export class PictureProviderService {
  private constructor() {
    logger.info('Picture Provider created!')
  }

  public savePicture = async (record: Record<string, string>): Promise<Response> => {
    await checkState()
    if (Object.keys(record).length !== 2) {
      logger.info("Parameters didn't work!")
      return { status: false, data: undefined }
    }

    let savedPicture

    try {
      savedPicture = await pictureModel.create(record)
    } catch (err) {
      logger.error('An error occurred: %s', err)
      prettyPrintError(err)
      return { status: false, data: undefined }
    }

    return { status: true, data: savedPicture }
  }

  public findPictures = async (ctx: HttpContext) => {
    await checkState()
    let queryParams = ctx.request.qs()

    const options = {
      page: Number(queryParams.page) || 1,
      limit: Number(queryParams.limit) || 10,
      collation: {
        locale: 'en',
      },
    }

    try {
      return ctx.response.status(200).json(await pictureModel.paginate({}, options))
    } catch (error) {
      console.error(error)
      return ctx.response.status(500).json({ message: 'An error occurred' })
    }
  }

  /**
   * Get the count of all pictures
   * @param ctx - The HTTP context
   * @returns The response object containing the count of all pictures
   */
  public getCount = async (ctx: HttpContext) => {
    await checkState()

    try {
      const count = await pictureModel.estimatedDocumentCount()
      logger.info(`Total pictures: ${count}`)
      return ctx.response.status(200).json({ count })
    } catch (error) {
      logger.error('An error occurred: %s', error)
      prettyPrintError(error)
      logger.info('Request failed while getting the Count of pages!')
      return ctx.response.status(500).json({ message: 'An error occurred' })
    }
  }

  /**
   * Get all unique diagnoses
   * @param ctx - The HTTP context
   * @returns The response object containing all unique diagnoses
   */
  public getLabels = async (ctx: HttpContext) => {
    try {
      return cache.getOrSet({
        key: 'labels',
        factory: async () => {
          const diagnosis = await pictureModel.distinct('diagnosis')
          return JSON.stringify(diagnosis)
        },
        ttl: '12h',
      })
    } catch (error) {
      logger.error('An unexpected error has occured!')
      return ctx.response
        .status(500)
        .send({ message: 'An unexpected error occured while collecting labels!' })
    }
  }

  public async postPicture(ctx: HttpContext){
    const file = ctx.request.file('file', {
      extnames: ['json'],
      size: '5mb',
    })

    if (!file || !file.isValid){
      logger.info("Provided file was not correct!");
      return ctx.response.badRequest("File wrong format!");
    }

    await ctx.request.multipart.onFile('file', {}, async (part) => {
      const stream = part;
      let fileData = Buffer.alloc(0)

      for await (const chunk of stream){
        fileData = Buffer.concat([fileData, chunk])
      }

      const content = fileData.toString('utf-8');
      const jsonData: Record<string, any> = JSON.parse(content);

      const status = await this.savePicture(jsonData);

      return ctx.response.ok({
        messages: 'File uploaded and processed!',
        data: status.data
      });
    });

    await ctx.request.multipart.process();

    return ctx.response.created();
  }

}
