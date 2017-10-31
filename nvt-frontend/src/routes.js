import Vue from 'vue'
import VueRouter from 'vue-router'
import Combinations from './components/combination/combinations'
import NewCombination from './components/combination/create-combination'
import EditCombination from './components/combination/edit-combination'
import StaticAnimation from './components/combination/animations/static-animation'
import DynamicAnimation from './components/combination/animations/dynamic-animation'
import AirlineSearch from './components/airlines/airline-search'
import AirlineNetwork from './components/airlines/airline-network'
import AirportSearch from './components/airports/airport-search'
import AirportComparison from './components/airports/airport-comparison'
import AirportVicinityComparison from './components/airports/airport-vicinity-comparison'

Vue.use(VueRouter)

let mainPageResolver = (to, from, next) => {
    next('/combinations')
}


const router = new VueRouter({
    mode: 'history',
    linkActiveClass: 'active',
    routes: [
        {
            path: '/',
            component: Combinations,
            beforeEnter: (to, from, next) => {
                mainPageResolver(to, from, next)
            }
        },
        {
            path: '/combinations',
            component: Combinations,
            name: 'combinations'
        },
        {
            path: '/combinations/new',
            component: NewCombination,
            name: 'new-combination'
        },
        {
            path: '/combinations/edit',
            component: EditCombination,
            name: 'edit-combination'
        },
        {
            path: '/combinations/static',
            component: StaticAnimation,
            name: 'static-animation'
        },
        {
            path: '/combinations/dynamic',
            component: DynamicAnimation,
            name: 'dynamic-animation'
        },
        {
            path: '/airlines',
            component: AirlineSearch,
            name: 'airlines'
        },
        {
            path: '/airline-network',
            component: AirlineNetwork,
            name: 'airline-network'
        },
        {
            path: '/airport-data',
            component: AirportSearch,
            name: 'airport-data'
        },
        {
            path: '/airport-comparison',
            component: AirportComparison,
            name: 'airport-comparison'
        },
        {
            path: '/airport-vicinity-comparison',
            component: AirportVicinityComparison,
            name: 'airport-vicinity-comparison'
        },
        {
            path: '*',
            redirect: '/'
        }
    ]
})

export default router