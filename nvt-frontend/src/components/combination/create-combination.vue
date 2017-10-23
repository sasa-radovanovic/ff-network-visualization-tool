<template lang="pug">
    div
        snackbar(:text="toasterText", :show="showSnackbar")
        v-flex.mt-5.mb-5(xs8, offset-xs2)
            v-card
                v-card-media(src="/static/card-header-3.png", height="200px")
                v-card-title(primary-title)
                    h5 New combination
                v-card-text
                    v-stepper(v-model="e1")
                        v-stepper-header
                            v-stepper-step(step="1", :complete="e1 > 1") Basic information
                            v-divider
                            v-stepper-step(step="2", :complete="e1 > 2") Routes
                            v-divider
                            v-stepper-step(step="3") Overview
                        v-stepper-content.mt-2(step="1")
                            v-card.mb-5
                                v-form(v-model="valid", @submit.prevent="")
                                    v-text-field(
                                    label="Name",
                                    v-model="name",
                                    :rules="nameRules",
                                    required
                                    )
                                v-layout(row, wrap)
                                    v-flex(xs6)
                                        p.text-xs-center
                                            v-icon.color-span.text-xs-center(:style="{ color: `rgb(${red}, ${green}, ${blue})` }") flight_land
                                        v-layout.mt-5.pr-5(row, justify-center)
                                            v-text-field(v-model="red", type="number", label="Red")
                                            v-text-field(v-model="blue", type="number", label="Blue")
                                            v-text-field(v-model="green", type="number", label="Green")
                                            v-text-field(v-model="hex", type="text", label="Hex", disabled)
                                    v-flex(xs6)
                                        v-flex(xs12)
                                            v-slider(label="R", :max="255", v-model="red")
                                        v-flex(xs12)
                                            v-slider(label="G", :max="255", v-model="green")
                                        v-flex(xs12)
                                            v-slider(label="B", :max="255", v-model="blue")
                                        v-flex(xs12)

                            v-btn(color="primary", @click.native="e1 = 2", :disabled="!valid") Next

                        v-stepper-content(step="2")
                            v-card.mb-5
                                combination-rotations(@rotation-change="rotationsUpdate")
                            v-btn(color="primary", @click.native="e1 = 3", :disabled="rotations.length === 0") Next
                            v-btn(flat, @click.native="e1 = 1") Back
                        v-stepper-content(step="3")
                            v-card.mb-5
                                new-combination-overview(:rotations="rotations", :name="name", :color="hex")
                            v-btn(color="primary", @click.native="createCombination") Create
                            v-btn(flat, @click.native="e1 = 2") Back


</template>


<script>

    import CombinationRotations from './rotations/combination-rotations.vue'
    import NewCombinationOverview from './new-combination-overview.vue'
    import { createRotation } from './../../services/combination-service'
    import Snackbar from './../../common/components/snackbar.vue'

    export default {
        components:{
            CombinationRotations,
            NewCombinationOverview,
            Snackbar
        },
        data() {
            return {
                valid: false,
                rotations: [],
                name: '',
                nameRules: [
                    (v) => !!v || 'Name is required',
                    (v) => v.length <= 30 || 'Name must be less than 30 characters'
                ],
                red: 64,
                green: 128,
                blue: 0,
                e1: 0,
                toasterText: '',
                showSnackbar: false
            }

        },
        computed: {
            hex() {
                return "#" + this.componentToHex(this.red) + this.componentToHex(this.green) + this.componentToHex(this.blue);
            }
        },
        methods: {
            componentToHex(c) {
                var hex = c.toString(16);
                return hex.length == 1 ? "0" + hex : hex;
            },
            rotationsUpdate(rotations) {
                this.rotations = rotations
            },
            createCombination() {
                createRotation(this.name, this.hex, this.rotations).then(rsp => {
                    this.toasterText = 'Combination successfully created'
                    this.showSnackbar = !this.showSnackbar
                    this.$router.push({name: 'combinations', params : {newCombination : true}})
                }).catch(err => {
                    console.log(err)
                    this.toasterText ='Error creating combination'
                    this.showSnackbar = !this.showSnackbar
                })
            }
        }
    }
</script>


<style scoped>
    .color-span {
        font-size: 80px;
        margin-right: auto;
        margin-left: auto;
    }
</style>