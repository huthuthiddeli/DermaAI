import type { HttpContext } from '@adonisjs/core/http'
import logger from '@adonisjs/core/services/logger'
import { PictureProviderService } from '#services/picture_provider_service'
import { inject } from '@adonisjs/core'

@inject()
export default class PicturesController {
  constructor(protected PictureProvider: PictureProviderService) {}

  public async postPicture({ request, response }: HttpContext): Promise<void> {
  let fileProcessed = false
  let result: any = null

  try {
    request.multipart.onFile('file', {}, async (part) => {
      fileProcessed = true

      const stream = part
      let fileData = Buffer.alloc(0)

      for await (const chunk of stream) {
        fileData = Buffer.concat([fileData, chunk])
      }

      try {
        const content = fileData.toString('utf-8')
        const jsonData: Record<string, any> = JSON.parse(content)

        // Save the data
        const status = await this.PictureProvider.savePicture(jsonData)
        result = {
          message: 'File uploaded and processed in-memory',
          data: status.data,
        }
      } catch (err) {
        logger.error('Error parsing JSON:', err)
        return response.badRequest('Invalid JSON content')
      }
    })

    await request.multipart.process()

    if (!fileProcessed) {
      return response.badRequest('No file uploaded')
    }

    return response.ok(result)
  } catch (error) {
    logger.error('Error processing file:', error)
    return response.internalServerError('Failed to process file')
  }
}



  public async getPicture(ctx: HttpContext) {
    return await this.PictureProvider.findPictures(ctx)
  }

  public async getCount(ctx: HttpContext) {
    return await this.PictureProvider.getCount(ctx)
  }

  public async getLabels(ctx: HttpContext) {
    return ctx.response.send(await this.PictureProvider.getLabels(ctx))
  }
}
