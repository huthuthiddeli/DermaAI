import type { HttpContext } from '@adonisjs/core/http'
import logger from '@adonisjs/core/services/logger'
import { promises as fs } from 'fs'
import { PictureProvider } from '../../providers/picture-provider.js';

export default class PictureController {
  public async postPicture({ request, response }: HttpContext): Promise<void> {
    const file = request.file('file', {
      extnames: ['json'],
      size: '5mb'
    });

    // Check if the file exists
    if (!file) {
      return response.badRequest('No file uploaded');
    }

    // Validate file
    if (!file.isValid) {
      return response.badRequest(file.errors);
    }

    // Move the file to a temporary location
    const filePath = `./tmp/${file.clientName}`;
    await file.move('./tmp', { name: file.clientName });

    // Read the content of the file
    try {
      const content = await fs.readFile(filePath, 'utf-8'); // Read the file content as a string
      const jsonData: Record<string, string> = JSON.parse(content); // Parse the JSON content

      // Print the JSON content to the console
      let status = await (await PictureProvider.getInstance()).savePicture(jsonData)

      logger.info(`Upload to database status: ${status.status}`)

      return response.ok({ message: 'File uploaded and content read successfully', data: status.data });
    } catch (error) {
      console.error('Error reading the file:', error);
      return response.internalServerError('Failed to read file content');
    }
  }


  public async getPicture(ctx: HttpContext) {
    return await (await PictureProvider.getInstance()).findPictures(ctx);
  }
}