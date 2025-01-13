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
import { saveUser, validateUser } from '../app/providers/userProvider.js'
import PostsController from '#controllers/posts_controller'
import { HttpContext } from '@adonisjs/core/http'
import { findPictures } from '../app/providers/pictureProvider.js'
import AutoSwagger from "adonis-autoswagger";
import swagger from "#config/swagger";


router.get('/', async () => {
  return {
    hello: 'world',
  }
})

// returns swagger in YAML
router.get("/swagger", async () => {
  return AutoSwagger.default.docs(router.toJSON(), swagger);
});

// Renders Swagger-UI and passes YAML-output of /swagger
router.get("/docs", async () => {
  return AutoSwagger.default.ui("/swagger", swagger);
  // return AutoSwagger.default.scalar("/swagger"); to use Scalar instead
  // return AutoSwagger.default.rapidoc("/swagger", "view"); to use RapiDoc instead (pass "view" default, or "read" to change the render-style)
});


router.post('/picture', [PostsController, 'picture'])

router.get('/picture', async (ctx: HttpContext) => {
  logger.info("Request received")
  return await findPictures(ctx);
})

router.post('/saveUser', async (ctx: HttpContext) => {
  return await saveUser(ctx)
});

router.post('/validateUser', async (ctx: HttpContext) => {
  return await validateUser(ctx); 
});