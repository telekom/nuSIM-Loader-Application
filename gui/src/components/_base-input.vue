<script>
export default {
  props: {
    type: {
      type: String,
      default: 'text',
    },
    value: {
      type: [String, Number],
      default: '',
    },
    label: {
      type: String,
      default: '',
    },
  },
  computed: {
    listeners() {
      return {
        ...this.$listeners,
        input: event => this.$emit('input', event.target.value),
      }
    },
  },
}
</script>

<template>
    <div class="form-input-set">
        <label>{{ label }}</label>
        <input
                :type="type"
                :value="value"
                class="form-input"
                v-on="listeners"
        >
    </div>


</template>
