import type { HttpContext } from '@adonisjs/core/http'
import logger from '@adonisjs/core/services/logger'
import { PictureProviderService } from '#services/picture_provider_service'
import { inject } from '@adonisjs/core'

@inject()
export default class PicturesController {
  constructor(protected PictureProvider: PictureProviderService) {}

  public async postPicture({ request, response }: HttpContext): Promise<void> {
    const file = request.file('file', {
      extnames: ['json'],
      size: '5mb',
    })

    // Check if the file exists
    if (!file) {
      return response.badRequest('No file uploaded')
    }

    // Validate file
    if (!file.isValid) {
      return response.badRequest(file.errors)
    }

    // Move the file to a temporary location
    await file.move('./tmp', { name: file.clientName })

    // Read the content of the file
    try {
      // const content = await fs.readFile(filePath, 'utf-8'); // Read the file content as a string
      let content = '' // <-- REMOVE WHEN WORKING
      const jsonData: Record<string, string> = JSON.parse(content) // Parse the JSON content

      // Print the JSON content to the console
      let status = await this.PictureProvider.savePicture(jsonData)

      logger.info(`Upload to database status: ${status.status}`)

      return response.ok({
        message: 'File uploaded and content read successfully',
        data: status.data,
      })
    } catch (error) {
      console.error('Error reading the file:', error)
      return response.internalServerError('Failed to read file content')
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
