import router from '@adonisjs/core/services/router'
import AutoSwagger from "adonis-autoswagger";
import swaggerConfig from "#config/swagger";
import app from '@adonisjs/core/services/app';
import { PictureProvider } from '../app/providers/picture-provider.js';
import { UserProvider } from '../app/providers/user-provider.js';
import { connectToDatabase } from '../app/utils/db-funcs.js';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

//----------FOR SWAGGER DOCUMENTATION -----------------
const UserController = () => import('#controllers/Http/UserController');
const PictureController = () => import("#controllers/Http/PictureController");

//----------COMPLETE BOOTUP-----------------
app.ready(async () => {
  await connectToDatabase();
  await PictureProvider.getInstance();
  await UserProvider.getInstance();
})

router.get('/', async () => {
  return {
    hello: 'world',
  }
})

// Get the directory name using import.meta.url
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// returns swagger in YAML
router.get("/swagger", async () => {
  const swaggerPath = path.resolve(__dirname, '../config/swagger.yaml');
  const swaggerContent = fs.readFileSync(swaggerPath, 'utf8');
  return swaggerContent;
  // return AutoSwagger.default.docs(router.toJSON(), swaggerConfig);
});

// Renders Swagger-UI and passes YAML-output of /swagger
router.get("/docs", async () => {
  return AutoSwagger.default.ui("/swagger", swaggerConfig);
  // return AutoSwagger.default.scalar("/swagger"); to use Scalar instead
  // return AutoSwagger.default.rapidoc("/swagger", "view"); to use RapiDoc instead (pass "view" default, or "read" to change the render-style)
});

// Add prefix to UserController routes
router.group(() => {
  router.get('/getAllUsers', [UserController, 'getAllUsers']);
  router.post('/saveUser', [UserController, 'saveUser']);
  router.post('/validateUser', [UserController, 'validateUser']);
  router.post('/mfa', [UserController, 'getMfaFromUser'])
  router.post('/switchMfa', [UserController, 'activateMfa'])
  router.delete('/collectionClear', [UserController, 'clearCollection'])
}).prefix('/user');

// Add prefix to PictureController routes
router.group(() => {
  router.post('/picture', [PictureController, 'postPicture']);
  router.get('/picture', [PictureController, 'getPicture']);
  router.get('/count', [PictureController, 'getCount']);
  router.get('/labels', [PictureController, 'getLabels'])
}).prefix('/picture');