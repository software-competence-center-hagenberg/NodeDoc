<template>
  <v-dialog v-model="dialog" scrollable width="800">
    <template v-slot:activator="{on, attrs}">
    <v-btn
      small
      color="primary"
      v-bind="attrs"
      v-on="on"
    >Tutorial</v-btn>
    </template>
    <v-card>
      <iframe v-bind:src="tutorialUrl" frameborder="0" style="width: 100%; height: 50vh; max-height: 90vh"></iframe>
      <v-card-actions>
          <v-btn
            color="blue darken-1"
            text
            @click="dialog = false"
          >
            Close
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            color="blue darken-1"
            text
            v-bind:href="tutorialUrl" target="_blank"
          >
            Open in new Window
          </v-btn>
        </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: "DemoTutorialDialog",
  data() {
    return {
      dialog: false,
      tutorialUrl: "/tutorial/index.html"
    };
  },
}
</script>