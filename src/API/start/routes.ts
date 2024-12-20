/*
|--------------------------------------------------------------------------
| Routes file
|--------------------------------------------------------------------------
|
| The routes file is used for defining the HTTP routes.
|
*/

import router from '@adonisjs/core/services/router'
import logger from '@adonisjs/core/services/logger'
// import {getAllPictues} from '../app/providers/pictureProvider.js'
import saveUser from '../app/providers/userProvider.js'
import PostsController from '#controllers/posts_controller'
import { HttpContext } from '@adonisjs/core/http'
import { findPictures } from '../app/providers/pictureProvider.js'

router.get('/', async () => {
  return {
    hello: 'world',
  }
})

router.post('/picture', [PostsController, 'picture'])

router.get('/picture', async (ctx: HttpContext) => {
  logger.info("Request received")
  return await findPictures(ctx);
})

router.post('/saveUser', async ({request}) => {
  
  let body = request.body()


  return await saveUser(body)
})