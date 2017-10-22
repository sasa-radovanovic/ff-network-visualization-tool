<template lang="pug">
    div
        v-snackbar(:timeout="timeout",
                :top="y === 'top'",
                :bottom="y === 'bottom'",
                :right="x === 'right'",
                :left="x === 'left'",
                :multi-line="mode === 'multi-line'",
                :vertical="mode === 'vertical'",
                v-model="snackbar")
            | {{ text }}
            v-btn(flat, color="pink", @click.native="snackbar = false") Close

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

    export default {
        components:{
            CombinationRotations,
            NewCombinationOverview
        },
        data() {
            return {
                valid: false,
                rotations: [],
                name: '',
                nameRules: [
                    (v) => !!v || 'Name is required',
                    (v) => v.length <= 15 || 'Name must be less than 15 characters'
                ],
                red: 64,
                green: 128,
                blue: 0,
                e1: 0,
                snackbar: false,
                text: '',
                y: 'top',
                x: null,
                mode: '',
                timeout: 4000
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
                    this.text = 'Combination successfully created'
                    this.snackbar = true
                    this.$router.push("{'name': 'combinations'}")
                }).catch(err => {
                    console.log(err)
                    this.text ='Error creating combination'
                    this.snackbar = true
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