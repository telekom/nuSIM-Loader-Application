<template>
    <v-dialog
            v-model="dialog"
            :max-width="options.width"
            scrollable
            @keydown.esc="cancel()">
        <v-card>
            <v-card-title class="headline grey lighten-2">Retrieve Profiles</v-card-title>
            <v-card-text style="height: 300px;">
                <v-list two-line>
                    <template v-for="(item) in items">
                        <v-list-tile
                                :key="item.referenceId"
                                avatar
                                @click="agree(item)"
                        >
                            <v-list-tile-avatar>
                                <img 
                                        :src="icon()"
                                        :class="Number(new Date()) > item.availableAt.epochSecond * 1000 ? 'green' : 'red'">
                            </v-list-tile-avatar>

                            <v-list-tile-content>
                                <v-list-tile-title>
                                    <b>ID {{ item.referenceId }} -
                                    Available at {{ instantFormat(item.availableAt) }}</b>
                                </v-list-tile-title>
                                <v-list-tile-sub-title>
                                    <div>#{{ item.requestCount }} requested at {{ instantFormat(item.requestedAt) }}
                                    </div>
                                    <div>
                                        <span><b>RefInfo 1: </b>{{ item.refInfo1 || '-' }}<b>&nbsp;|&nbsp;</b></span>
                                        <span><b>RefInfo 2: </b>{{ item.refInfo2 || '-' }}<b>&nbsp;|&nbsp;</b></span>
                                        <span><b>RefInfo 3: </b>{{ item.refInfo3 || '-' }}</span>
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
                        @click="cancel()"
                >Cancel
                </button>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import instantFormat from '@utils/instantFormat'
import icon from '@assets/icons/round-move_to_inbox-24px.svg'

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
    instantFormat(instant) {
      return instantFormat(instant)
    },
    open(data, options) {
      this.dialog = true
      this.options = Object.assign(this.options, options)

      this.items = data

      return new Promise((resolve, reject) => {
        this.resolve = resolve
        this.reject = reject
      })
    },
    agree(item) {
      this.resolve({
        referenceId: item.referenceId,
      })
      this.dialog = false
    },
    cancel() {
      this.reject(true)
      this.dialog = false
    },
    icon() {
      return icon
    },
  },
}
</script>
