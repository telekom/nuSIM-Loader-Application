<script>
import Layout from '@layouts/main'
import { authMethods } from '@state/helpers'
import appConfig from '@src/app.config'

export default {
  page: {
    title: 'Log in',
    meta: [{ name: 'description', content: `Log in to ${appConfig.title}` }],
  },
  components: { Layout },
  data() {
    return {
      username: '',
      password: '',
      authError: null,
      tryingToLogIn: false,
    }
  },
  methods: {
    ...authMethods,
    // Try to log the user in with the username
    // and password they provided.
    tryToLogIn() {
      this.tryingToLogIn = true
      // Reset the authError if it existed.
      this.authError = null
      return this.logIn({
        username: this.username,
        password: this.password,
      })
        .then(token => {
          this.tryingToLogIn = false

          // Redirect to the originally requested page, or to the home page
          this.$router.push(this.$route.query.redirectFrom || { name: 'home' })
        })
        .catch(error => {
          this.tryingToLogIn = false
          this.authError = error
        })
    },
  },
}
</script>

<template>
    <Layout>

        <form 
                @submit.prevent="tryToLogIn"
        >
            <div class="col-l-4 offset-l-4 col-m-6 offset-m-3 col-s-10 offset-s-1">
                <h1 class="panel-title">
                    T-Rex Login
                </h1>
                <BaseInput
                        v-model="username"
                        name="username"
                        label="Benutzername"
                />
                <BaseInput
                        v-model="password"
                        name="password"
                        type="password"
                        label="Passwort"
                />
                <BaseButton
                        :disabled="tryingToLogIn"
                        type="submit"
                >
                    <BaseIcon
                            v-if="tryingToLogIn"
                            name="sync"
                            spin
                    />
                    <span v-else>Log in</span>
                </BaseButton>
                <p v-if="authError">
                    There was an error logging in to your account.
                </p>
            </div>
        </form>
    </Layout>
</template>
