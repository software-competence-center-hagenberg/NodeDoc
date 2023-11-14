<template>
  <v-form @submit.prevent="addTemplate" style="margin-top: 15px">
    <div
      style="display: flex"
      @drop.prevent="handleDroppedTemplateFile"
      @dragover.prevent
    >
      <v-file-input
        :disabled="this.$store.state.isDemo"
        class="text-field"
        v-model="template.file"
        accept=".html"
        label="Upload a new .html-template file"
        outlined
        dense
      ></v-file-input>
      <v-btn icon :disabled="this.$store.state.isDemo || template.file == null" @click="addTemplate">
        <font-awesome-icon
          icon="plus-circle"
          size="2x"
          :style="{ color: template.file == null ? 'gray' : 'green' }"
        />
      </v-btn>
    </div>
    <v-text-field
      :disabled="this.$store.state.isDemo"
      class="text-field"
      label="Html template description"
      v-model="template.description"
      outlined
      dense
    ></v-text-field>
  </v-form>
</template>

<script>
import Vuetify from "vuetify";

export default {
  name: "TemplateUpload",
  vuetify: new Vuetify(),
  data: () => ({
    template: {
      file: null,
      description: null,
    },
  }),

  methods: {
    /**
     * Checks if template files where dropped and converts the dropped files
     * list to an array, for which every object is checked if it is an html
     * file. Then the templateFile is set to the dropped file.
     */
    handleDroppedTemplateFile(e) {
      let droppedFiles = e.dataTransfer.files;
      if (!droppedFiles || droppedFiles.length != 1) return;
      // spread operator for converting list to array
      [...droppedFiles].forEach((f) => {
        if (f.type == "text/html") this.template.file = f;
      });
    },

    addTemplate() {
      if (this.template.file != null) {
        this.$emit("upload-template", this.template);
      }
    },

    unsetTemplateFile() {
      this.template.file = null;
      this.template.description = null;
    },
  },
};
</script>

<style scoped>
.text-field >>> .v-input__control {
  height: 3rem;
}
</style>