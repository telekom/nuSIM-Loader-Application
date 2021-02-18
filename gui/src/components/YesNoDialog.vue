<template>
    <v-dialog
            v-model="dialog"
            :max-width="options.width"
            @keydown.esc="cancel()"
            @keydown.enter="agree()">
        <v-card>
            <v-card-title class="headline grey lighten-2">
                <v-avatar
                        size="32"
                >
                    <img :src="icon">
                </v-avatar>
                <span style="margin-left:1em;">{{ dialogHeadline }}</span>
            </v-card-title>
            <v-card-text>{{ dialogMessage }}</v-card-text>
            <v-card-actions>
                <v-spacer/>
                <button
                        type="button"
                        class="btn btn-default"
                        @click="agree()"
                >Yes
                </button>
                <button
                        type="button"
                        class="btn"
                        @click="cancel()"
                >No
                </button>
            </v-card-actions>

        </v-card>
    </v-dialog>
</template>

<script>
import infoIcon from '@assets/icons/outline-info-24px.svg'

/**
 * Vuetify Confirm Dialog component
 *
 * Insert component where you want to use it:
 * <confirm ref="confirm"></confirm>
 *290
 * Call it:
 * this.$refs.confirm.open('Delete', 'Are you sure?', { color: 'red' }).then((confirm) => {});
 *
 * Alternatively you can place it in main App component and access it globally via this.$root.$confirm
 * <template>
 *   <v-app>
 *     ...
 *     <confirm ref="confirm"></confirm>
 *   </v-app>
 * </template>
 *
 * mounted() {
 *   this.$root.$confirm = this.$refs.confirm.open;
 * }
 */
export default {
  name: 'InfoDialog',
  data() {
    return {
      dialog: false,
      dialogHeadline: null,
      dialogMessage: null,
      resolve: null,
      reject: null,

      options: {
        color: 'primary',
        width: '70%',
      },
    }
  },
  computed: {
    icon: function() {
      return infoIcon
    },
  },
  methods: {
    open(title, message, options) {
      this.dialogHeadline = title
      this.dialogMessage = message
      this.options = Object.assign(this.options, options)
      this.dialog = true
      return new Promise((resolve, reject) => {
        this.resolve = resolve
        this.reject = reject
      })
    },
    agree() {
      this.resolve()
      this.dialog = false
    },
    cancel() {
      this.reject(true)
      this.dialog = false
    },
  },
}
</script>
