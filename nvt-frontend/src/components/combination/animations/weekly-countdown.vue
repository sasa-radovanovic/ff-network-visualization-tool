<template lang="pug">
    v-layout(row, wrap)
        v-flex(xs6)
            v-alert(color="info", icon="info", value="true")
                span {{formattedDay}} {{formattedHour}}:{{formattedMinute}}
        v-flex.pl-1(xs6)
            v-alert(color="info", icon="info", value="true")
                span Speed {{speedRatio}}x
</template>


<script>
    export default {
        props: ['day', 'hours', 'minutes', 'speedRatio'],
        data () {
            return {
                weekday: {}
            }
        },
        created() {
            this.weekday[0] = "Monday";
            this.weekday[1] = "Tuesday";
            this.weekday[2] = "Wednesday";
            this.weekday[3] = "Thursday";
            this.weekday[4] = "Friday";
            this.weekday[5] = "Saturday";
            this.weekday[6] = "Sunday";
        },
        computed: {
            formattedDay() {
                return this.weekday[Number(this.day)]
            },
            formattedHour() {
                if (Number(this.hours) < 10) {
                    return '0' + Number(this.hours)
                } else {
                    return Number(this.hours)
                }
            },
            formattedMinute() {
                if (Number(this.minutes) < 10) {
                    return '0' + Number(this.minutes)
                } else {
                    return Number(this.minutes)
                }
            }
        }
    }
</script>


<style scoped>

</style>