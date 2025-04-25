/*
|--------------------------------------------------------------------------
| Routes file
|--------------------------------------------------------------------------
|
| The routes file is used for defining the HTTP routes.
|
*/

import router from '@adonisjs/core/services/router'
import UsersController from '#controllers/users_controller'
import PicturesController from '#controllers/pictures_controller'
import PredictionController from '#controllers/predictions_controller'
import path from 'path'
import fs from 'fs'
import { fileURLToPath } from 'url'
import Autoswagger from 'adonis-autoswagger'
import swagger from '#config/swagger'

// Get the directory name using import.meta.url
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// // returns swagger in YAML
router.get('/swagger', async () => {
  const swaggerPath = path.resolve(__dirname, '../config/swagger.yaml')
  const swaggerContent = fs.readFileSync(swaggerPath, 'utf8')
  return swaggerContent
})

// // Renders Swagger-UI and passes YAML-output of /swagger
router.get('/docs', async () => {
  return Autoswagger.default.ui('/swagger', swagger)
})

// Add prefix to UserController routes
router
  .group(() => {
    router.get('/getAllUsers', [UsersController, 'getAllUsers'])
    router.post('/saveUser', [UsersController, 'saveUser'])
    router.post('/validateUser', [UsersController, 'validateUser'])
    router.post('/mfa', [UsersController, 'getMfaFromUser'])
    router.post('/switchMfa', [UsersController, 'activateMfa'])
    router.post('/setAdmin', [UsersController, 'checkIfAdmin'])
    router.post('/setUser', [UsersController, 'checkIfUser'])
    router.delete('/clearCollection', [UsersController, 'clearCollection'])
  })
  .prefix('/user')

// Add prefix to PredictionController routes
router
  .group(() => {
    router.post('/savePrediction', [PredictionController, 'savePrediction'])
    router.post('/loadPrediction', [PredictionController, 'loadPrediction'])
  })
  .prefix('/prediction')

// Add prefix to PictureController routes
router
  .group(() => {
    router.post('/picture', [PicturesController, 'postPicture'])
    router.get('/picture', [PicturesController, 'getPicture'])
    router.get('/count', [PicturesController, 'getCount'])
    router.get('/labels', [PicturesController, 'getLabels'])
  })
  .prefix('/picture')

router.get('/', async () => {
  return {
    hello: 'world',
  }
})
