<template lang="pug">
    div
        snackbar(:text="snackBarText", :show="snackBarShow")
        v-form.new-rotation-form(ref="form", v-model="valid")
            v-layout(row, wrap)
                v-flex(xs12)
                    h5 Add new rotation
                v-flex(xs3, md3)

                    v-text-field(
                    label="Origin airport",
                    v-model="newRotation.departureAirport",
                    :rules="newRotationRules.airportRules",
                    required,
                    disabled
                    )
                v-flex(xs3, md3)

                    airport-select-combination.mt-2(label="Select airport", action="Select", @return-action="onOriginSelected")

                v-flex(xs3, md3)

                    v-text-field(
                    label="Destination airport",
                    v-model="newRotation.arrivalAirport",
                    :rules="newRotationRules.airportRules",
                    required,
                    disabled
                    )

                v-flex(xs3, md3)

                    airport-select-combination.mt-2(label="Select airport", action="Select", @return-action="onDestinationSelected")

                v-flex(md5)

                    v-text-field(
                    type="number",
                    label="Flight length [min]",
                    v-model="newRotation.flightLength",
                    :rules="newRotationRules.flightLength",
                    required
                    )

                v-flex(md5, offset-md1)
                    v-menu(lazy,
                    :close-on-content-click="false",
                    v-model="menu2",
                    transition="scale-transition",
                    offset-y,
                    full-width,
                    :nudge-right="40",
                    max-width="290px",
                    min-width="290px")
                        v-text-field(slot="activator",
                        label="Local departure time",
                        v-model="newRotation.departureTime",
                        readonly,
                        :rules="newRotationRules.localDepartureTimeRules")
                        v-time-picker(v-model="newRotation.departureTime", autosave, format="24hr")

                v-flex(md4)
                    v-checkbox(label="Monday", v-model="newRotation.days[0]")
                    v-checkbox(label="Tuesday", v-model="newRotation.days[1]")
                    v-checkbox(label="Wednesday", v-model="newRotation.days[2]")
                v-flex(md4)
                    v-checkbox(label="Thursday", v-model="newRotation.days[3]")
                    v-checkbox(label="Friday", v-model="newRotation.days[4]")
                v-flex(md4)
                    v-checkbox(label="Saturday", v-model="newRotation.days[5]")
                    v-checkbox(label="Sunday", v-model="newRotation.days[6]")

                v-flex(md12)
                    v-btn(primary, :disabled="!valid || daysValidated !== true", @click="addAction") Add
        h5.mt-2.ml-3 Rotations
        v-data-table.elevation-1(:headers='headers', :items='rotations', hide-actions)
            template(slot='items', slot-scope='props')

                td {{props.item.id}}

                td {{props.item.origin}}

                td {{props.item.destination}}

                td {{props.item.frequency}}

                td {{props.item.departureTime}}

                td {{props.item.flightLength}}

                td
                    v-btn(color="red", dark, @click="removeAction(props.item.id)")
                        v-icon(dark, left) delete
                        | Remove
</template>


<script>

    import {getRotations, removeRotation, addRotation} from './../../../services/rotation-service'
    import AirportSelectCombination from './../airports/airports-select-dialog.vue'
    import Snackbar from './../../../common/components/snackbar.vue'

    export default {
        components: {
            AirportSelectCombination,
            Snackbar
        },
        props: ['combinationId'],
        data() {
            return {
                menu2: false,
                snackBarText: '',
                snackBarShow: '',
                newRotationRules: {
                    airportRules: [
                        (v) => (v !== null && !!v) || 'Airport is required',
                        (v) => (v !== null && v.length > 3) || 'Airport must have IATA code'
                    ],
                    flightLength: [
                        (v) => (v !== null && !!v) || 'Flight length is required',
                        (v) => (v !== null && v > 0) || 'Flight length must be > 0'
                    ],
                    localDepartureTimeRules: [
                        (v) => (v !== null && !!v) || 'Departure time is required'
                    ]
                },
                newRotation: {
                    departureAirport: '',
                    departureAirportIata: '',
                    arrivalAirport: '',
                    arrivalAirportIata: '',
                    flightLength: 60,
                    departureTime: null,
                    days: [
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false
                    ]
                },
                valid: false,
                headers: [
                    {
                        text: 'ID',
                        align: 'left',
                        sortable: true,
                        value: 'ID'
                    },
                    {
                        text: 'Origin',
                        align: 'left',
                        sortable: true,
                        value: 'Origin'
                    },
                    {
                        text: 'Destination',
                        align: 'left',
                        sortable: true,
                        value: 'Destination'
                    },
                    {
                        text: 'Frequency',
                        align: 'left',
                        sortable: true,
                        value: 'Frequency'
                    },
                    {
                        text: 'Departure time',
                        align: 'left',
                        sortable: true,
                        value: 'Departure time'
                    },
                    {
                        text: 'Flight length [min]',
                        align: 'left',
                        sortable: true,
                        value: 'Flight length'
                    }
                ],
                rotations: [],
                newCombination: true,
                insertIndex: 1
            }
        },
        created() {
            if (this.combinationId !== undefined) {
                this.newCombination = false
                this.retrieveRotations()
            }
        },
        computed: {
            daysValidated() {
                var activeDays = this.newRotation.days.find(d => {
                    return d === true
                })
                return activeDays
            }
        },
        methods: {
            retrieveRotations() {
                this.rotations = []
                getRotations(this.combinationId).then(rsp => {
                    rsp.forEach(r => {
                        let frequency = ''
                        for (let d = 1; d <= 7; d++) {
                            if (r.dayMap[d] === true) {
                                frequency += d + '/'
                            }
                        }
                        frequency = frequency.substring(0, frequency.length - 1);
                        this.rotations.push({
                            'id': r.id,
                            'origin': r.originIataCode,
                            'destination': r.destinationIataCode,
                            'flightLength': r.flightTime,
                            'departureTime': r.utcDepartureTime,
                            'frequency': frequency
                        })
                    })
                }).catch(err => {
                    this.snackBarText = "Error retrieving rotations"
                    this.snackBarShow = !this.snackBarShow
                    this.rotations = []
                })
            },
            resetNewRotation() {
                this.newRotation.departureTime = '12:00'
                this.newRotation.flightLength = 60
                this.newRotation.days = [
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
                ]
                this.newRotation.departureAirportIata = ''
                this.newRotation.arrivalAirportIata = ''
            },
            onOriginSelected(item) {
                if (item === undefined || item === null) {
                    return
                }
                this.newRotation.departureAirportIata = item.iataCode
                this.newRotation.departureAirport = item.name + ' (' + item.iataCode + ')'
            },
            onDestinationSelected(item) {
                if (item === undefined || item === null) {
                    return
                }
                this.newRotation.arrivalAirportIata = item.iataCode
                this.newRotation.arrivalAirport = item.name + ' (' + item.iataCode + ')'
            },
            removeAction(index) {
                if (!this.newCombination) {
                    removeRotation(index).then(rsp => {
                        this.snackBarText = "Removed rotation from combination"
                        this.snackBarShow = !this.snackBarShow
                        this.retrieveRotations()
                    }).catch(err => {
                        this.snackBarText = "Error occured while removing rotation from combination"
                        this.snackBarShow = !this.snackBarShow
                    })
                } else {
                    this.rotations = this.rotations.filter(r => {
                        return r.id !== index
                    })
                    this.$emit('rotation-change', this.rotations)
                }
            },
            addAction() {
                if (!this.newCombination) {
                    var frequency = '';
                    for (var i = 1; i <= 7; i++) {
                        if (this.newRotation.days[i - 1] === true) {
                            frequency = frequency + i + '/'
                        }
                    }
                    frequency = frequency.substring(0, frequency.length - 1);
                    addRotation(this.newRotation.departureAirportIata, this.newRotation.arrivalAirportIata, frequency, this.newRotation.departureTime,
                        this.newRotation.flightLength, this.combinationId).then(rsp => {
                        this.snackBarText = "Rotation added to Combination"
                        this.snackBarShow = !this.snackBarShow
                        this.retrieveRotations()
                    }).catch(err => {
                        this.snackBarText = "Error occured while adding rotation from combination"
                        this.snackBarShow = !this.snackBarShow
                        this.retrieveRotations()
                    })
                } else {
                    var frequency = '';
                    for (var i = 1; i <= 7; i++) {
                        if (this.newRotation.days[i - 1] === true) {
                            frequency = frequency + i + '/'
                        }
                    }
                    frequency = frequency.substring(0, frequency.length - 1);
                    this.rotations.push({
                        'id': this.insertIndex,
                        'origin': this.newRotation.departureAirportIata,
                        'destination': this.newRotation.arrivalAirportIata,
                        'departureTime': this.newRotation.departureTime,
                        'flightLength': this.newRotation.flightLength,
                        'frequency': frequency
                    })
                    this.insertIndex++
                    this.resetNewRotation()
                    this.$refs.form.reset()
                    this.$emit('rotation-change', this.rotations)
                }
            }
        }
    }
</script>


<style scoped>
    .new-rotation-form {
        background-color: lightblue;
        padding: 20px;
    }
</style>