import { useLoaderData } from 'react-router-dom'
import {
  homeContainer,
  homeDescription,
  homeHeadingPrimary,
  homeHeadingSecondary,
  homeSection,
  homeSectionHero,
  homeSectionReverse,
  homeWrapperHero,
} from './home.module.css'
import { HomeHeader } from '@/layout/header/home-header'
import { UserResponse } from '@/api/user/user'
import { Footer } from '@/layout/footer/footer'
import { Button } from '@/components/button/button'

import HeroImage from '@/assets/svg/hero.svg'
import Feature1 from '@/assets/svg/feature_1.svg'
import Feature2 from '@/assets/svg/feature_2.svg'
import Feature3 from '@/assets/svg/feature_3.svg'
import FooterImage from '@/assets/svg/footer_1.svg'

export function Home() {
  const user = useLoaderData() as UserResponse

  return (
    <>
      <HomeHeader user={user} />
      <main className={homeContainer}>
        <div className={`${homeSection} ${homeSectionHero}`}>
          <div className={homeWrapperHero}>
            <h1 className={homeHeadingPrimary}>将想法装进BOX</h1>
            <p className={homeDescription}>毫无压力 想记就记</p>
            <Button>立刻加入</Button>
          </div>
          <div>
            <img src={HeroImage} alt="noteverso hero image" />
          </div>
        </div>

        <div className={homeSection}>
          <div>
            <h2 className={homeHeadingSecondary}>不分类别，随时记录</h2>
            <p className={homeDescription}>告别分类整理的压力，尽情记录</p>
          </div>
          <div className="flex justify-center">
            <img src={Feature1} alt="noteverso hero image" />
          </div>
        </div>

        <div className={`${homeSection} ${homeSectionReverse}`}>
          <div>
            <h2 className={homeHeadingSecondary}>回顾过去，轻松创意</h2>
            <p className={homeDescription}>定期回顾，从中获取新的视角和想法</p>
          </div>
          <div className="flex justify-center">
            <img src={Feature2} alt="noteverso feature image" />
          </div>
        </div>

        <div className={homeSection}>
          <div>
            <h2 className={homeHeadingSecondary}>添加标签，方便查找</h2>
            <p className={homeDescription}>通过自定义标签，快速查找笔记</p>
          </div>
          <div className="flex justify-center">
            <img src={Feature3} alt="noteverso feature image" />
          </div>
        </div>

        <div className={homeSection}>
          <div className="flex justify-center">
            <img src={FooterImage} alt="noteverso footer image" />
          </div>
          <div>
            <h2 className={homeHeadingPrimary}>将想法装进BOX</h2>
            <p className={homeDescription}>毫无压力，想记就记</p>
          </div>
          <div>
            <Button>立刻加入</Button>
          </div>
        </div>

      </main>
      <Footer />
    </>
  )
}
