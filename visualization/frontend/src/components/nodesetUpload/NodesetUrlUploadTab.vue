<template>
  <div>
    <v-text-field
      class="text-field"
      label="Nodeset URL"
      v-model="urlNodeset.url"
      :disabled="disabled"
      outlined
      dense
      clearable
    ></v-text-field>

    <v-text-field
      class="text-field"
      label="Bearer Token (if needed)"
      v-model="urlNodeset.token"
      :disabled="disabled"
      outlined
      dense
      clearable
    ></v-text-field>
  </div>
</template>

<script>
import Vuetify from "vuetify";

export default {
  name: "NodesetUrlUploadTab",
  vuetify: new Vuetify(),
  components: {},

  data: () => ({
    urlNodeset: {
      url: null,
      token: null,
    },
  }),
  props: {
    disabled: Boolean, //nodesetFile != null
  },
  watch: {
    "urlNodeset.url": function () {
      this.emitUrlObjectChange();
    },
    "urlNodeset.token": function () {
      this.emitUrlObjectChange();
    },
  },
  methods: {
    emitUrlObjectChange() {
      this.$emit("url-object-changed", this.urlNodeset);
    },

    unsetNodesetUrlObject() {
      this.urlNodeset = {
        url: null,
        token: null,
      };
    },
  },
};
</script>

<style scoped>
.text-field >>> .v-input__control {
  height: 3rem;
}
</style>