import type { HttpContext } from '@adonisjs/core/http'
import logger from '@adonisjs/core/services/logger'
import { promises as fs } from 'fs'
import { findPictures, savePicture } from '#providers/pictureProvider'

export default class PictureController {
  /**
   * @swagger-
   * /picture:
   *   post:
   *     tags:
   *       - Picture
   *     summary: Upload a picture
   *     description: Upload a picture and save it to the database
   *     requestBody:
   *       required: true
   *       content:
   *         multipart/form-data:
   *           schema:
   *             type: object
   *             properties:
   *               file:
   *                 type: string
   *                 format: binary
   *     responses:
   *       200:
   *         description: File uploaded and content read successfully
   *         content:
   *           application/json:
   *             schema:
   *               type: object
   *               properties:
   *                 message:
   *                   type: string
   *                 data:
   *                   type: object
   *       400:
   *         description: Bad request
   *       500:
   *         description: Internal server error
   */
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
      let status = await savePicture(jsonData)
      logger.info(`Upload to database status: ${status.status}`)

      return response.ok({ message: 'File uploaded and content read successfully', data: status.data });
    } catch (error) {
      console.error('Error reading the file:', error);
      return response.internalServerError('Failed to read file content');
    }
  }

  /**
   * @swagger
   * /picture:
   *   get:
   *     tags:
   *       - Picture
   *     summary: Get pictures
   *     description: Retrieve pictures from the database
   *     responses:
   *       200:
   *         description: Pictures retrieved successfully
   *         content:
   *           application/json:
   *             schema:
   *               type: array
   *               items:
   *                 type: object
   *       500:
   *         description: Internal server error
   */
  async getPicture(ctx: HttpContext) {
    return await findPictures(ctx);
  }
}