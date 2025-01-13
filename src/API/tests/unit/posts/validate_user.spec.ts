import { test } from '@japa/runner'
import testUtils from '@adonisjs/core/services/test_utils'
import { hashPassword } from '../../../app/utils/hash.js'
import { saveUser, validateUser, clearCollection } from '../../../app/providers/userProvider.js'
import { ObjectId } from 'mongodb'


test.group('Posts validate user', () => {
  test('Validate User received from request', async ({ assert }) => {

    let user: Record<string, any> = {
      firstname: "hugo",
      lastname: "bob",
      email: "bob@test.at",
      password: "nothashedPassword"
    };

    user.password = await hashPassword(user.password);
    const ctx = await testUtils.createHttpContext();
    ctx.request.updateBody(user);

    await saveUser(ctx);

    let aws: Record<string, any> = ctx.response.getBody();

    let a = {
      firstname: aws.firstname,
      lastname: aws.lastname,
      email: aws.email,
      password: aws.password,
      id: new ObjectId(aws._id),
      __v: aws.__v
    };

    ctx.request.updateBody(a);

    await validateUser(ctx);

    let response = {
      _id: aws._id,
      firstname: 'hugo',
      lastname: 'bob',
      email: 'bob@test.at',
      __v: 0
    }

    assert.deepEqual(ctx.response.getBody(), response);

    clearCollection(ctx);

    if(await ctx.response.getStatus() === 500){
      assert.fail();
    }
  })
})