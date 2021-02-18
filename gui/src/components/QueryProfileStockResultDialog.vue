<template>
    <v-dialog
            v-model="dialog"
            :max-width="options.width"
            scrollable
            @keydown.esc="agree()"
            @keydown.enter="agree()">
        <v-card>
            <v-card-title class="headline grey lighten-2">Profile Stock Information</v-card-title>
            <v-card-text style="height: 300px;">
                <v-list two-line>
                    <template v-for="(item, index) in items">
                        <v-list-tile
                                :key="index"
                                avatar
                        >
                            <v-list-tile-avatar>
                                <img :src="icon()">
                            </v-list-tile-avatar>

                            <v-list-tile-content>
                                <v-list-tile-title><b>Available: {{ item.available }}</b>
                                </v-list-tile-title>
                                <v-list-tile-sub-title>
                                    <div>
                                        <span><b>RefInfo1: </b>{{ item.refInfo1 }}</span>
                                        <span><b>RefInfo2: </b>{{ item.refInfo2 }}</span>
                                        <span><b>RefInfo3: </b>{{ item.refInfo3 }}</span>
                                    </div>
                                </v-list-tile-sub-title>
                            </v-list-tile-content>
                        </v-list-tile>
                    </template>
                </v-list>
            </v-card-text>
            <v-card-actions>
                <v-spacer/>
                <button
                        type="button"
                        class="btn btn-default"
                        @click="agree()"
                >OK
                </button>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import icon from '@assets/icons/outline-info-24px.svg'

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
  name: 'RetrieveProfileDialog',
  data() {
    return {
      dialog: false,
      items: [],
      resolve: null,
      reject: null,
      options: {
        color: 'primary',
        width: 590,
      },
    }
  },
  methods: {
    open(data, options) {
      this.dialog = true
      this.options = Object.assign(this.options, options)

      this.items = data

      return new Promise((resolve, reject) => {
        this.resolve = resolve
        this.reject = reject
      })
    },
    agree() {
      this.dialog = false
    },
    icon() {
      return icon
    },
  },
}
</script>
