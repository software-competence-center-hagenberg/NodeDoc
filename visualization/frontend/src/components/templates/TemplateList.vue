<template>
  <v-card class="html-template-list-card" :disabled="this.$store.state.isDemo" :loading="uploading">
    <v-card-title class="card-title">Uploaded template files:</v-card-title>
    <v-card-text class="card-text">
      <v-list dense v-if="templates" class="html-template-list">
        <v-list-item
          dense
          v-for="item in templates"
          :key="item.templateName"
          style="height: 3rem"
        >
          <v-list-item-content style="padding: 3px 0">
            <v-list-item-title v-html="item.templateName"></v-list-item-title>
            <v-list-item-subtitle
              v-html="item.templateDescription"
            ></v-list-item-subtitle>
          </v-list-item-content>

          <v-list-item-action>
            <v-btn icon @click="deleteItem(item)">
              <font-awesome-icon icon="trash" />
            </v-btn>
          </v-list-item-action>
        </v-list-item>
      </v-list>
      <!-- TODO: Upload position fixed on bottom -->
      <template-upload
        @upload-template="uploadTemplate"
        ref="templateUpload"
        class="template-upload"
      />
    </v-card-text>
  </v-card>
</template>

<script>
import Vuetify from "vuetify";
import TemplateRestResource from "../rest/TemplateRestResource";
import TemplateUpload from "./TemplateUpload.vue";

const templateRestResourceService = new TemplateRestResource();

export default {
  name: "TemplateList",
  vuetify: new Vuetify(),
  components: {
    TemplateUpload,
  },
  data: () => ({
    templates: Array,
    uploading: false,
  }),

  mounted: function () {
    this.uploading = true;
    this.loadTemplates();
  },

  methods: {
    /**
     * Loads the html templates.
     */
    loadTemplates: function () {
      templateRestResourceService.loadTemplates().then((result) => {
        this.templates = result;
        this.uploading = false;
        this.$refs.templateUpload.unsetTemplateFile();
      });
    },

    uploadTemplate: function (template) {
      templateRestResourceService
        .uploadTemplate(template.file, template.description)
        .then(() => {
          this.loadTemplates();
        })
        .catch((error) => {
          this.loadTemplates();
          this.$emit("upload-error", error.response.data, "red lighten-3");
        });
    },

    deleteItem: function (item) {
      this.$emit("delete-item", item);
    },
  },
};
</script>

<style scoped>
.html-template-list-card {
  min-height: 19rem;
  width: 100%;
  float: left;
}

.card-text {
  min-height: 17rem;
  display: flex;
  flex-direction: column;
}

.html-template-list {
  overflow: auto;
}

</style>