<template>
    <div class="new-storage-container">
        <p>{{ $t("storagePage.alias") }}:ccccccccc</p>
        <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            class="source-form"
            label-position="top"
        >
            <el-form-item
                :label="$t('storagePage.groupName')"
                prop="groupName"
                class="form-input-item"
            >
                <el-input v-model="form.groupName"></el-input>
            </el-form-item>
            <el-form-item
                :label="$t('storagePage.groupDescription')"
                class="form-input-item"
            >
                <el-input v-model="form.description"></el-input>
            </el-form-item>
            <el-form-item
                :label="$t('storagePage.ttl')"
                class="form-input-item"
            >
                <el-input
                    v-model="form.ttl"
                    type="number"
                    class="ttl-input"
                ></el-input>
                <el-select
                    v-model="form.ttlUnit"
                    class="ttl-input unit"
                    placeholder="  "
                >
                    <el-option
                        :label="$t('storagePage.secondLabel')"
                        value="second"
                    >
                    </el-option>
                    <el-option
                        :label="$t('storagePage.minuteLabel')"
                        value="minute"
                    >
                    </el-option>
                    <el-option
                        :label="$t('storagePage.hourLabel')"
                        value="hour"
                    >
                    </el-option>
                    <el-option :label="$t('storagePage.dayLabel')" value="day">
                    </el-option>
                    <el-option
                        :label="$t('storagePage.weekLabel')"
                        value="week"
                    >
                    </el-option>
                    <el-option
                        :label="$t('storagePage.monthLabel')"
                        value="month"
                    >
                    </el-option>
                    <el-option
                        :label="$t('storagePage.yearLabel')"
                        value="year"
                    >
                    </el-option>
                </el-select>
                <i class="tips">注:不填代表无限大</i>
            </el-form-item>
        </el-form>
        <div class="submit-btns">
            <el-button>取消</el-button>
            <el-button type="primary">确定</el-button>
        </div>
    </div>
</template>

<script>
// @ is an alias to /src
import { onMounted, ref, reactive } from "vue";
import {
    ElForm,
    ElFormItem,
    ElInput,
    ElSelect,
    ElOption,
    ElButton,
} from "element-plus";
import { useI18n } from "vue-i18n";
// import { useRouter } from "vue-router";

export default {
    name: "NewStorages",
    setup() {
        const { t } = useI18n();
        // const router = useRouter();
        let formRef = ref(null);
        const rules = reactive({
            groupName: [
                {
                    required: true,
                    message: t(`storagePage.aliasEmptyTip`),
                    trigger: "change",
                },
            ],
        });
        let form = reactive({
            groupName: "",

            description: "",
            ttl: "",
            ttoUnit: "秒",
        });
        onMounted(() => {});

        return {
            // t,
            formRef,
            rules,
            form,
        };
    },
    components: {
        ElForm,
        ElFormItem,
        ElInput,
        ElSelect,
        ElOption,
        ElButton,
    },
};
</script>

<style scoped lang="scss">
.new-storage-container {
    text-align: left;
    padding: 20px;
    .el-form {
        margin-top: 20px;
        .form-input-item {
            width: 450px;
        }
        .ttl-input {
            display: inline-block;
            width: 350px;
        }
        .unit {
            width: 90px;
            margin-left: 10px;
        }
        .tips {
            color: rgba(34, 34, 34, 0.4);
            font-size: 12px;
        }
    }

    .submit-btns {
        .el-button {
            width: 110px;
        }
    }
}
</style>
