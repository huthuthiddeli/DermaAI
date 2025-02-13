import { test } from '@japa/runner'
import { assert } from '@japa/assert'
import testUtils from '@adonisjs/core/services/test_utils'
import { UserProvider } from '../../../app/providers/user-provider.js'
import { userDataModel } from '../../../app/Models/userDataCollection.js'
test.group('Posts validate user', (group) => {
  group.setup(async () => {
    await userDataModel.deleteMany({})
  })

  test('Validate User received from request', async ({ assert }) => {
    let user: Record<string, any> = {
      email: "test",
      password: "test"
    }

    const ctx = await testUtils.createHttpContext()
    ctx.request.updateBody(user)

    const userProvider = await UserProvider.getInstance()
    await userProvider.saveUser(ctx)

    let savedUser: Record<string, any> = ctx.response.getBody()

    let loginRequest = {
      email: savedUser.email,
      password: "test"
    }

    ctx.request.updateBody(loginRequest)
    await userProvider.validateUser(ctx)

    let response = {
      _id: savedUser._id,
      email: 'test',
      __v: 0
    }

    const responseBody = ctx.response.getBody()
    delete responseBody.password // Remove password from response for comparison

    assert.deepEqual(responseBody, response)

    if (await ctx.response.getStatus() === 500) {
      assert.fail()
    }
  })
})