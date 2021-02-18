<template>
    <v-dialog
            v-model="dialog"
            :max-width="options.width"
            @keydown.esc="cancel()"
            @keydown.enter="agree()">
        <v-card>
            <v-card-title class="headline grey lighten-2">Query Profile Stock</v-card-title>
            <v-card-text>
                <v-text-field

                        v-model="refInfo1"
                        label="RefInfo1"
                        hide-details
                />
                <v-text-field

                        v-model="refInfo2"
                        label="RefInfo2"
                        hide-details
                />
                <v-text-field

                        v-model="refInfo3"
                        label="RefInfo3"
                        hide-details
                />
            </v-card-text>
            <v-card-actions>
                <v-spacer/>
                <button
                        type="button"
                        class="btn btn-default"
                        @click="agree()"
                >OK
                </button>
                <button
                        type="button"
                        class="btn btn-default"
                        @click="cancel()"
                >Cancel
                </button>
            </v-card-actions>

        </v-card>
    </v-dialog>
</template>

<script>
import store from '@utils/dialogStore'

/**
 * Vuetify Confirm Dialog component
 *
 * Insert component where you want to use it:
 * <confirm ref="confirm"></confirm>
 *
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
  name: 'QueryProfileStockDialog',
  data() {
    return {
      dialog: false,
      refInfo1: '',
      refInfo2: '',
      refInfo3: '',
      resolve: null,
      reject: null,
      options: {
        color: 'primary',
        width: 290,
      },
    }
  },
  methods: {
    open(title, message, options) {
      this.dialog = true
      this.options = Object.assign(this.options, options)

      const d = store.load('queryProfileStockDialog')

      this.refInfo1 = d.refInfo1
      this.refInfo2 = d.refInfo2
      this.refInfo3 = d.refInfo3

      return new Promise((resolve, reject) => {
        this.resolve = resolve
        this.reject = reject
      })
    },
    agree() {
      store.store('queryProfileStockDialog', {
        refInfo1: this.refInfo1,
        refInfo2: this.refInfo2,
        refInfo3: this.refInfo3,
      })

      this.resolve({
        refInfo1: this.refInfo1,
        refInfo2: this.refInfo2,
        refInfo3: this.refInfo3,
      })
      this.dialog = false
    },
    cancel() {
      this.reject(true)
      this.dialog = false
    },
  },
}
</script>
