<template>
  <v-app>
    <v-main>
      <v-container>
        <h1 class="mb-4 text-center">To-Do List</h1>
        <v-row cols="12">
          <v-col>
            <v-checkbox v-model="todosTableMetadata.hideCompleted"
              label="Hide Completed" />
          </v-col>
        </v-row>
        <v-row class="mb-4"
          cols="12">
          <v-col>
            <v-data-table :expanded.sync="todosTableMetadata.expanded"
              :headers="todosTableMetadata.headers"
              :items="filteredTodos"
              :loading="todosTableMetadata.loading"
              no-data-text="No to-dos available"
              show-expand
              :single-expand="true">
              <template v-slot:[`item.createdDt`]="{ item }">
                {{ formatDt(item.createdDt, 'YYYY-MM-DD hh:mm A') }}
              </template>
              <template v-slot:[`item.completedDt`]="{ item }">
                {{ formatDt(item.completedDt, 'YYYY-MM-DD hh:mm A') }}
              </template>
              <template v-slot:[`item.actions`]="{ item }">
                <v-btn class="mr-2"
                  color="primary"
                  :disabled="todosTableMetadata.loading"
                  fab
                  x-small
                  @click="onTodosTableItemDelete(item.id)">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
                <v-btn :disabled="!!item.completedDt || todosTableMetadata.loading"
                  color="primary"
                  fab
                  x-small
                  @click="onTodosTableItemComplete(item)">
                  <v-icon>mdi-check</v-icon>
                </v-btn>
              </template>
              <template v-slot:expanded-item="{ headers, item }">
                <td class="pa-4"
                  :colspan="headers.length">
                  <strong>Description:</strong><br />
                  {{ item.desc || "n/a" }}
                </td>
              </template>
            </v-data-table>
          </v-col>
        </v-row>
        <h2 class="mb-4">Add To-Do</h2>
        <v-form v-model="addTodoFormMetadata.valid"
          ref="addTodoForm">
          <v-row class="mb-4">
            <v-col cols="12"
              sm="6">
              <v-text-field v-model="addTodoFormMetadata.models.todo.summary"
                :counter="50"
                label="Summary"
                required
                :rules="addTodoFormMetadata.rules.summaryRules" />
            </v-col>
            <v-col cols="12">
              <v-textarea v-model="addTodoFormMetadata.models.todo.desc"
                :counter="2000"
                label="Description"
                :rules="addTodoFormMetadata.rules.descRules" />
            </v-col>
          </v-row>
          <div class="text-center">
            <v-btn @click="onAddTodoFormSubmit"
              color="primary"
              :disabled="!addTodoFormMetadata.valid"
              :loading="addTodoFormMetadata.submitting">Add</v-btn>
          </div>
        </v-form>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import axios from "axios";
import moment from "moment";
import _ from "lodash";

export default {
  name: "App",
  computed: {
    backendUrl() {
      return this.getEnv("VUE_APP_BACKEND_URL");
    },
    filteredTodos() {
      return this.todosTableMetadata.hideCompleted
        ? this.models.todos.filter((e) => !e.completedDt)
        : this.models.todos;
    },
  },
  data: () => ({
    addTodoFormMetadata: {
      expanded: [],
      models: {
        todo: {
          desc: "",
          summary: "",
        },
      },
      rules: {
        descRules: [
          (v) =>
            v.length <= 2000 || "Description must be less than 2000 characters",
        ],
        summaryRules: [
          (v) => !!v || "Summary is required",
          (v) => v.length <= 50 || "Summary must be less than 50 characters",
        ],
      },
      submitting: false,
      valid: false,
    },
    todosTableMetadata: {
      headers: [
        {
          text: "Summary",
          value: "summary",
          width: "40%",
        },
        {
          text: "Created",
          value: "createdDt",
          width: "20%",
        },
        {
          text: "Completed",
          value: "completedDt",
          width: "20%",
        },
        {
          text: "Actions",
          value: "actions",
          width: "20%",
        },
        { text: "", value: "data-table-expand" },
      ],
      hideCompleted: false,
      loading: false,
    },
    models: {
      todos: [],
    },
  }),
  methods: {
    formatDt: function (dt) {
      return dt ? moment(dt).format("YYYY-MM-DD hh:mm A") : "-";
    },
    getEnv: function (name) {
      return window.config[name] || process.env[name];
    },
    getTodos: function () {
      this.todosTableMetadata.loading = true;
      axios
        .get(this.backendUrl + "/api/todos")
        .then((res) => {
          this.models.todos = _.orderBy(res.data, ["createdDt"]);
          this.todosTableMetadata.loading = false;
        })
        .catch((e) => {
          console.error(e.message);
          this.todosTableMetadata.loading = false;
        });
    },
    onAddTodoFormSubmit: function () {
      this.addTodoFormMetadata.submitting = true;
      let reqPayload = JSON.parse(
        JSON.stringify(this.addTodoFormMetadata.models.todo)
      );
      if (reqPayload.desc !== null && reqPayload.desc.length === 0) {
        reqPayload.desc = null;
      }
      axios
        .post(this.backendUrl + "/api/todos", reqPayload)
        .then((res) => {
          let location = res.headers["location"];
          return axios.get(location);
        })
        .then((res2) => {
          this.models.todos.push(res2.data);
          this.addTodoFormMetadata.models.todo = {
            desc: "",
            summary: "",
          };
          this.$refs.addTodoForm.resetValidation();
          this.addTodoFormMetadata.submitting = false;
        })
        .catch((e) => {
          console.error(e.message);
          this.addTodoFormMetadata.submitting = false;
        });
    },
    onTodosTableItemComplete: function (todo) {
      this.todosTableMetadata.loading = true;
      const todos = this.models.todos;
      const id = todo.id;
      let reqPayload = JSON.parse(JSON.stringify(todo));
      reqPayload.completedDt = moment().valueOf();
      axios
        .put(this.backendUrl + "/api/todos/" + id, reqPayload)
        .then(() => {
          return axios.get(this.backendUrl + "/api/todos/" + id);
        })
        .then((res2) => {
          const index = todos.findIndex((e) => e.id === id);
          todos.splice(index, 1, res2.data);
          this.todosTableMetadata.loading = false;
        })
        .catch((e) => {
          this.todosTableMetadata.loading = false;
          console.error(e.message);
        });
    },
    onTodosTableItemDelete: function (id) {
      this.todosTableMetadata.loading = true;
      const todos = this.models.todos;
      axios
        .delete(this.backendUrl + "/api/todos/" + id)
        .then(() => {
          const index = todos.findIndex((e) => e.id === id);
          todos.splice(index, 1);
          this.todosTableMetadata.loading = false;
        })
        .catch((e) => {
          this.todosTableMetadata.loading = false;
          console.error(e.message);
        });
    },
  },
  mounted: function () {
    this.getTodos();
  },
};
</script>
