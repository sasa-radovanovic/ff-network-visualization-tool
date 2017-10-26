<template lang="pug">
    v-dialog(v-model="dialog", persistent="", max-width="800px")
        v-btn(:style="{'color': current}", flat, slot="activator", @click="parseHex()") Change color
        v-card
            v-card-title
                span.headline Change color for {{iata}}
            v-card-text
                v-container(grid-list-md)
                    v-layout(wrap)
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
            v-card-actions
                v-spacer
                v-btn(color="blue darken-1", flat, @click.native="selectNewColor") Select
                v-btn(color="blue darken-1", flat, @click.native="closeDialog") Close

</template>


<script>
    export default {
        props: ['current', 'iata', 'name'],
        data () {
            return {
                dialog: false,
                red: '',
                green: '',
                blue: ''
            }
        },
        computed: {
            hex() {
                return "#" + this.componentToHex(this.red) + this.componentToHex(this.green) + this.componentToHex(this.blue);
            }
        },
        methods: {
            parseHex() {
                let parsed = this.hexToRgb(this.current)
                this.red = parsed.r
                this.green = parsed.g
                this.blue = parsed.b
                this.hex = this.current
            },
            closeDialog() {
                this.dialog = false
            },
            hexToRgb(hex) {
                var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
                return result ? {
                    r: parseInt(result[1], 16),
                    g: parseInt(result[2], 16),
                    b: parseInt(result[3], 16)
                } : null;
            },
            selectNewColor() {
                this.$emit('selected-color', {
                    'name' : this.name,
                    'iata' : this.iata,
                    'color' : this.rgbToHex(this.red, this.green, this.blue)
                })
                this.dialog = false
            },
            rgbToHex(r, g, b) {
                return "#" + this.componentToHex(r) + this.componentToHex(g) + this.componentToHex(b);
            },
            componentToHex(c) {
                var hex = c.toString(16);
                return hex.length == 1 ? "0" + hex : hex;
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