import { mapActions, mapGetters, mapState } from 'vuex'

export const authComputed = {
  ...mapState('auth', {
    currentUser: state => state.currentUser,
  }),
  ...mapGetters('auth', ['loggedIn']),
}

export const authMethods = mapActions('auth', ['logIn', 'logOut'])
