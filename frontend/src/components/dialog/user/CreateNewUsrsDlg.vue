<script setup lang="ts">
import type { PageVO, UserProfileDTO } from "@/api/types";
import type { FileUploadSelectEvent } from "primevue/fileupload";
import { createMultiUsrs } from "@/api/superadmin/user";
import { parse } from "csv-parse/browser/esm/sync";
import _ from "lodash";

/* Models */
const visible = defineModel<boolean>("visible");

/* Services */
const { success, info, error, remove } = useNotify();

// Emits
const emits = defineEmits<{ success: [] }>();

/* Reactive */
const refUsrTbl = useTemplateRef("refUsrTbl");
const busy = ref(false);
const users = ref<any[]>([]);

/* Actions */
const onFileSelected = (ev: FileUploadSelectEvent) => {
  const file = ev.files[0];
  const reader = new FileReader();
  reader.onload = () => {
    try {
      const data = parse(reader.result as string, {
        bom: true,
        columns: ["username", "displayName", "email", "password", "role"],
        skip_empty_lines: true
      });
      const uniqData = _(data)
        .uniqBy(({ username }: any) => username)
        .uniqBy(({ email }: any) => email)
        .filter(({ role }: any) => ["admin", "user"].includes(role))
        .map((row) => ({ ...row, role: row.role === "user" ? 1 : 2 }))
        .value();
      success(`${uniqData.length} user(s) loaded`);

      users.value = _([...users.value.slice(), ...uniqData])
        .uniqBy(({ username }: any) => username)
        .uniqBy(({ email }: any) => email)
        .value();

      refUsrTbl.value?.refresh();
    } catch (err: unknown) {}
  };
  reader.readAsText(file);
};
const fetchUsers = (page: number, limit: number, keyword?: string) =>
  ({
    total: users.value.length,
    list:
      _.chunk(users.value, limit)[page - 1]?.filter((row) => {
        keyword = keyword ?? "";
        return (
          row.username.includes(keyword) ||
          row.displayName.includes(keyword) ||
          row.email.includes(keyword)
        );
      }) ?? null
  }) satisfies PageVO<UserProfileDTO>;
const submit = async () => {
  // Try to update
  busy.value = true;
  const msg = info("Updating");

  try {
    await createMultiUsrs({ list: users.value });
    visible.value = false;
    emits("success");
    success("Successfully updated");
  } catch (err: unknown) {
    error((err as Error).message, "Fail to Update");
  }

  remove(msg);
  busy.value = false;
};

/* Watches */
watch(visible, () => {
  if (!visible.value) {
    busy.value = false;
    users.value = [];
  }
});
</script>

<template>
  <Dialog
    v-model:visible="visible"
    header="Create New Users"
    :closable="false"
    modal>
    <form @submit.prevent="submit">
      <Panel
        class="mb-4"
        header="CSV File Format Tips"
        :collapsed="true"
        toggleable>
        <p>
          Table <b>SHOULD EXACTLY</b> contains 5 columns, each corresponds to:
        </p>
        <p class="indent-4">
          <i class="text-green-500">Username</i>,
          <i class="text-green-500">DisplayName</i>,
          <i class="text-green-500">Email</i>,
          <i class="text-green-500">Password</i>,
          <i class="text-green-500">Role</i>
        </p>
        <p>
          For role column, available values are
          <i class="text-green-500">admin</i> and
          <i class="text-green-500">user</i>
        </p>
        <p>Table headers are unnecessary</p>
        <p>Table rows would be uniquified by usernames and emails</p>
      </Panel>

      <div class="flex gap-4 mb-4">
        <FileUpload
          accept=".csv"
          class="text-sm"
          choose-label="Append CSV file"
          mode="basic"
          @select="onFileSelected"
          auto
          custom-upload />
        <Button
          icon="pi pi-trash"
          label="Clear"
          severity="danger"
          size="small"
          @click="
            () => {
              users = [];
              refUsrTbl?.refresh();
            }
          "></Button>
      </div>

      <UsrTbl ref="refUsrTbl" :refresh-fn="fetchUsers">
        <template #operations>
          <Column header-class="w-12">
            <template #body="{ data }">
              <div class="gap-2 hidden justify-end group-hover:flex">
                <OperationButton
                  icon="pi-trash"
                  label="Delete"
                  severity="danger"
                  @click="
                    () => {
                      users = users.filter(
                        ({ username }: any) => username !== data.username
                      );
                      refUsrTbl?.refresh();
                    }
                  " />
              </div>
            </template>
          </Column>
        </template>
      </UsrTbl>

      <div class="flex justify-end gap-2 mt-4">
        <Button
          label="Cancel"
          severity="secondary"
          size="small"
          type="button"
          :disabled="busy"
          @click="visible = false"></Button>
        <Button
          label="Create"
          size="small"
          type="submit"
          :disabled="users.length === 0 || busy"
          :loading="busy"></Button>
      </div>
    </form>
  </Dialog>
</template>
