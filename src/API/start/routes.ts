/*
|--------------------------------------------------------------------------
| Routes file
|--------------------------------------------------------------------------
|
| The routes file is used for defining the HTTP routes.
|
*/

import router from '@adonisjs/core/services/router'
import { writeToDatabase, getAllFromDatabase } from '../app/providers/mongoprovider.js'
import logger from '@adonisjs/core/services/logger'
import {getAllPictues, savePicture} from '../app/providers/pictureProvider.js'
import saveUser from '../app/providers/userProvider.js'

router.get('/', async () => {
  return {
    hello: 'world',
  }
})

router.post('/article', async ({request, response}) => {
  let bodyText = request.body()
  let data = await writeToDatabase(bodyText)

  if(data.status){
    logger.info("Data has been created:\n%s", data.data)
    return data.data
  }

  response.status(400).send(data.data)
})

router.get('/any', async () => {
    return getAllFromDatabase()
  }
)

router.post('/picture', async ({request}) => {
    let body = request.body()

    for(let key in body){
      
      if(key == 'picture'){
        return await savePicture(body[key])
      }
    }
  }
)

router.get('/getAllPictures', async () => {

  let data = await getAllPictues()

  if(data.length != 0){
    return data;
  }

  return 0;
})

router.post('/saveUser', async ({request}) => {
  
  let body = request.body()


  return await saveUser(body)
})