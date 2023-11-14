<template>
  <v-radio-group class="templateRadio" v-model="selected">
    <v-radio
      v-for="item in templates"
      :key="item.templateName"
      :value="item.templateName"
      style="margin-left: 10px;"
    >
      <template v-slot:label>
        <div>
          <h4>{{ item.templateName }}</h4>
          <p style="font-size: 14px">{{ item.templateDescription }}</p>
        </div>
      </template>
    </v-radio>
  </v-radio-group>
</template>


<script>
import Vuetify from "vuetify";

export default {
  name: "NodesetTreeView",
  vuetify: new Vuetify(),
  data: () => ({
    selected: null,
  }),
  props: {
    templates: [],
  },
  watch: {
    selected: function () {
      this.$emit("template-selection-changed", this.selected);
    },
  },
  methods: {
    unsetSelectedTemplate() {
      this.selected = null;
    }
  }
};
</script>


<style scoped>
.templateRadio {
  height: 30vh;
  min-height: 200px;
  overflow: auto;
}
</style>