<script>
export default {
  props: {
    value: {
      type: [String, Number, Boolean, Array],
      default: '',
    },
    schema: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {
      e1: true,
    }
  },
  computed: {
    theValue: function() {
      return this.value
    },
  },
  methods: {
    updateValue(value) {
      this.$emit('input', value)
    },
    updateArrayValue(value) {
      const split = value.split(/ *, */)
      this.$emit('input', split)
    },
  },
}
</script>

<template>
    <div>

        <!--<v-list-tile>-->
        <!--<v-list-tile-content>-->
        <!--<v-list-tile-title>{{schema.meta.displayName}}</v-list-tile-title>-->
        <!--<div>-->
        <v-text-field
                v-if="schema.meta.type === 'text' || schema.meta.type === 'number'"
                :value="theValue"
                :rules="schema.meta.rules"
                :label="schema.meta.displayName"
                :required="schema.meta.required"
                hide-details
                @input="updateValue($event)"
        />
        <v-text-field
                v-if="schema.meta.type === 'array' "
                :value="theValue"
                :rules="schema.meta.rules"
                :label="schema.meta.displayName"
                :required="schema.meta.required"
                hide-details
                @input="updateArrayValue($event)"
        />
        <v-text-field
                v-if="schema.meta.type === 'password'"
                :value="theValue"
                :rules="schema.meta.rules"
                :label="schema.meta.displayName"
                :required="schema.meta.required"
                :append-icon="e1 ? 'visibility' : 'visibility_off'"
                :type="e1 ? 'password' : 'text'"
                hide-details
                @click:append="() => (e1 = !e1)"
                @input="updateValue($event)"
        />

        <v-checkbox
                v-else-if="schema.meta.type === 'boolean'"
                :input-value="theValue"
                :rules="schema.meta.rules"
                :label="schema.meta.displayName"
                :required="schema.meta.required"
                hide-details
                @change="updateValue($event)"
        />

        <!--<input type="checkbox" class="form-checkbox"-->
        <!--:value="value" v-on="listeners" v-if="schema.meta.type === 'boolean' ">-->

        <v-radio-group
                v-else-if="schema.meta.type === 'enum' && schema.meta.selection === 'single'"
                :input-value="theValue"
                :label="schema.meta.displayName"
                row
                hide-details
                @change="updateValue($event)"
        >
            <v-radio
                    v-for="e in schema.meta.enums"
                    :key="e.key"
                    :label="e.text"
                    :value="e.key"
            />
        </v-radio-group>

        <!--</div>-->
        <!--</v-list-tile-content>-->
        <!--</v-list-tile>-->
    </div>


</template>
