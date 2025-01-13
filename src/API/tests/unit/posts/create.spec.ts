import { test } from '@japa/runner'
import { hashPassword } from '../../../app/utils/hash.js';
import { clearCollection, getAllUsers, saveUser } from '../../../app/providers/userProvider.js';
import testUtils from '@adonisjs/core/services/test_utils';


test.group('Posts create', () => {
  test('hashes user password and creates entry in database', async ({ assert }) => {
    let user: Record<string, any> = {
      firstname: "jonas",
      lastname: "maier",
      email: "jonas.maier@htl-saalfelden.at",
      password: "9f735e0df9a1ddc702bf0a1a7b83033f9f7153a00c29de82cedadc9957289b05"
    };

    user.password = await hashPassword(user.password);
    let lengthBefore = (await getAllUsers()).length; 
    const ctx = await testUtils.createHttpContext();
    ctx.request.updateBody(user);
    await saveUser(ctx);
    let lenghtAfter = (await getAllUsers()).length - 1;

    assert.strictEqual(lengthBefore, lenghtAfter);
    await clearCollection(ctx);
    if(await ctx.response.getStatus() == 500){
      assert.fail();
    }
  })
})