// import { defineConfig, store, drivers } from '@adonisjs/cache'

// const cacheConfig = defineConfig({
//   default: 'redis',
  
//   stores: {

//     /**
//      * Cache data in-memory as the primary store and Redis as the secondary store.
//      * If your application is running on multiple servers, then in-memory caches
//      * need to be synchronized using a bus.
//      */
//     redis: store()
//       .useL1Layer(drivers.memory({ maxSize: '100mb' }))
//       .useL2Layer(drivers.redis({ connectionName: 'main' }))
//       .useBus(drivers.redisBus({ connectionName: 'main' })),
//   },
// })

// export default cacheConfig


// TODO: https://docs.adonisjs.com/guides/digging-deeper/cache