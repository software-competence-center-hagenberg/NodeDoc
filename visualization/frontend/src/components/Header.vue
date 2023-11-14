<template>
  <header>
    <img alt="SCCH logo" src="../assets/logo.png" height="30" />
    <span>OPC UA NodeSet Documentation-Generator</span>
    <span class="spacer"></span>
    <DemoTutorialDialog v-if="this.$store.state.isDemo"></DemoTutorialDialog>
    <span class="version">Version: {{ version }}</span>
  </header>
</template>


<script>
import DemoTutorialDialog from './dialogs/DemoTutorialDialog.vue';
import ApplicationRestResource from "./rest/ApplicationRestResource";

const applicationRestResourceService = new ApplicationRestResource();

export default {
  name: "Header",
  components: {
    DemoTutorialDialog
  },
  data: () => ({
    version: String,
  }),
  mounted: function () {
    this.getAppVersion();
  },
  methods: {
    
    getAppVersion: function () {
      applicationRestResourceService.getVersion().then((result) => {
        this.version = result;
      });
    },
  },
};
</script>

<style scoped>
header {
  margin: 0px;
  height: 2em;
  font-size: 1.75rem;
  display: flex;
  gap: 2em;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1rem;
}

span {
  color: rgb(255, 255, 255);
}
</style>