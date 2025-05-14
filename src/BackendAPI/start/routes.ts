/*
|--------------------------------------------------------------------------
| Routes file
|--------------------------------------------------------------------------
|
| The routes file is used for defining the HTTP routes.
|
*/

import router from '@adonisjs/core/services/router'
import path from 'path'
import fs from 'fs'
import { fileURLToPath } from 'url'
import Autoswagger from 'adonis-autoswagger'
import swagger from '#config/swagger'


const UsersController = () => import('#controllers/users_controller')
const PicturesController = () => import('#controllers/pictures_controller')
const PredictionController = () => import('#controllers/predictions_controller')

// Dynamically collect routes and generate Swagger documentation
router.get('/swagger', async () => {
  return Autoswagger.default.docs(router.toJSON(), swagger); // Collect routes dynamically
});

// Render Swagger UI
router.get('/docs', async () => {
  return Autoswagger.default.ui('/swagger', swagger); // Render Swagger UI
});

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
    Server: 'up',
  }
})
